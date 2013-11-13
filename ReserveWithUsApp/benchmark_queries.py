'''
Run ReserveWithUsApp benchmarks using Python.
'''
import subprocess
import time
import csv
import numpy as np
import datetime

class Timer(object):
    ''' Simple timer. '''

    def __init__(self):
        self._t_start = None
        self._t_stop = None

    def __enter__(self):
        self._t_start = time.time()
        return self


    def __exit__(self, exc_type, value, traceback):
        self._t_stop = time.time()

    @property
    def t_start(self):
        return self._t_start

    @property
    def t_stop(self):
        return self._t_stop

    @property
    def elapsed(self):
        if self._t_start is None:
            raise ValueError("Must start before querying time.")
        if self._t_stop is None:
            return time.time() - self._t_start
        else:
            return self._t_stop - self._t_start

    @property
    def isrunning(self):
        return self._process is not None


def send_commands(commands, host=None, port=None, timeout=5):
    '''
    Send commands to ReserveWithUsApp.

    Parameters
    ----------
    commands : string or iterable
        The commands to send.
    host : string
        The hostname to contact.
    port : short int
        The port to connect to.
    timeout : int
        Max number of seconds to wait per command.

    Returns
    -------
    results : iterable of string
        The lines returned.
    '''

    if commands is None:
        raise ValueError('Commands cannot be None.')

    # Set default args.
    if host is None:
        host = 'localhost'
    if port is None:
        port = 4444

    # Open netcat process.
    p = subprocess.Popen(['nc', host, str(port)],
            stdin=subprocess.PIPE,
            stdout=subprocess.PIPE, stderr=subprocess.PIPE)

    # Write either iterable commands or a single string.
    if hasattr(commands, '__iter__') is True:
        num_commands = len(commands)
        p.stdin.write('\n'.join(commands))
    else:
        num_commands = 1
        p.stdin.write(commands)
    p.stdin.close()

    # Poll process waiting for commands to complete.
    max_timeout = num_commands * timeout
    with Timer() as timer:
        while True:
            if p.poll() is not None:
                break
            if timer.elapsed > max_timeout:
                p.terminate()
                raise EnvironmentError('Timeout waiting for commands to complete.')
            else:
                time.sleep(0.001)

    # Check return code.
    if p.wait() != 0:
        raise EnvironmentError('Bad return code executing command.')

    # Return stdout
    return p.stdout.read().splitlines()


class ReserveWithUsAppSession(object):
    ''' Represent an active ReserveWithUsApp session. '''

    def __init__(self, jar_path):
        self._jar_path = jar_path
        self._process = None
        self._stdout = None
        self._stderr = None

    def __enter__(self):
        ''' Open ReserveWithUsApp process. '''

        self._process = subprocess.Popen(['java', '-jar', self.jar_path],
                stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        return self

    def __exit__(self, type, value, traceback):
        ''' Close ReserveWithUsApp process. '''

        self.terminate()

    def terminate(self):
        ''' Terminate the ReserveWithUsApp process. '''

        if self._process is not None:
            self._process.terminate()
            self._stdout = self._process.stdout.read().splitlines()
            self._stderr = self._process.stderr.read().splitlines()
            self._process = None

    @property
    def jar_path(self):
        return self._jar_path

    @property
    def stdout(self):
        return self._process.stdout

    @property
    def stderr(self):
        return self._process.stderr

    @property
    def isrunning(self):
        return self._process is not None


class RandomUniformCountryCity(object):
    ''' Sample uniformly from (country, city) tuples. '''

    def __init__(self):
        # Get country, city distribution.
        self._ccc = [(int(count), country, city)
                for count, country, city
                in csv.reader(open('query_data/country_city_counts.csv', 'r'))]
        self._ccc_counts = np.fromiter((r[0] for r in self._ccc), dtype=int)
        self._ccc_cumsum = np.cumsum(self._ccc_counts)
        self._total_hotels = self._ccc_cumsum[-1]

    @property
    def raw_data(self):
        return self._ccc

    @property
    def total_hotels(self):
        return self._total_hotels

    def get_country_city(self):
        return tuple(self._ccc[np.searchsorted(
            self._ccc_cumsum, np.random.randint(0, self._total_hotels))][1:])


class RandomLogNormalDateRange(object):
    ''' Sample date inerval from log-normal. '''

    @staticmethod
    def daterange(start_date, end_date):
        '''
        http://stackoverflow.com/questions/1060279/
            iterating-through-a-range-of-dates-in-python
        '''
        for n in range(int ((end_date - start_date).days)):
            yield start_date + datetime.timedelta(n)

    def __init__(self, range_mu=None, range_sig=None):
        if range_mu is None:
            self._mu = 1
        if range_sig is None:
            self._sig = 0.5
        # Get country, city distribution.
        date_of = lambda date: datetime.datetime.strptime(date, '%Y%m%d')
        self._dates_raw = [date_of(date)
                for _, date
                in csv.reader(open('query_data/date_counts.csv', 'r'))]
        self._min_date = min(self._dates_raw)
        self._max_date = max(self._dates_raw)
        self._dates = [time.strftime('%Y-%m-%d', d.timetuple())
                for d in self.daterange(self._min_date,
                    self._max_date + datetime.timedelta(1))]
        self._days = len(self._dates)

    def get_date_range(self):
        # Select a log-normal distributed interval.
        interval = np.clip(
            int(np.random.lognormal(self._mu, self._sig)), 1, self._days - 1)
        # Get the dates for the interval within the range of dates.
        i_begin = np.random.randint(0, self._days - 1)
        i_end = min(i_begin + interval, self._days - 1)
        return self._dates[i_begin], self._dates[i_end]


def mode_test_i_hotels(rwu_jar_path, num_queries):
    '''
    Test ReserveWithUs queries of the type
        i) Query on hotel based on country and city.

    Parameters
    ----------
    rwu_jar_path : string
        Path to ReserveWithUsApp jar.
    num_queries : number
        Number of queries to test.

    Returns
    -------
    times : list
        Elapsed time per query.
    '''

    # Get country, city distribution.
    rand_country_city = RandomUniformCountryCity()

    hotel_times = []

    # Open a session.
    with ReserveWithUsAppSession(rwu_jar_path) as session:

        for i in range(num_queries):

            country, city = rand_country_city.get_country_city()

            # Build command for hotels.
            query = 'command=search_hotels&country={}&city={}'.format(
                    country, city)

            # Run command and append elapsed time.
            with Timer() as timer:
                result = send_commands([query, 'command=get_hotels'], timeout=20)
            hotel_times.append(timer.elapsed)

            print query
            print 'hotel command {} of {}: time={:1.6f}s, len(result)={}'.format(
                    i+1, num_queries, timer.elapsed, len(result) - 3)

    return hotel_times

def mode_test_i_rooms(rwu_jar_path, num_queries):
    '''
    Test ReserveWithUs queries of the type
        i) Query on rooms based on country and city.

    Parameters
    ----------
    rwu_jar_path : string
        Path to ReserveWithUsApp jar.
    num_queries : number
        Number of queries to test.

    Returns
    -------
    times : list
        Elapsed time per query.
    '''

    # Get country, city distribution.
    rand_country_city = RandomUniformCountryCity()

    # Get date begin, end distribution.
    rand_date_range = RandomLogNormalDateRange()

    rooms_times = []

    # Open a session.
    with ReserveWithUsAppSession(rwu_jar_path) as session:

        for i in range(num_queries):

            country, city = rand_country_city.get_country_city()

            # Build command for rooms.
            date_start, date_stop = rand_date_range.get_date_range()
            query = ('command=search_rooms&country={}&city={}&'
                'date_start={}&date_stop={}&numrooms=1').format(
                    country, city, date_start, date_stop)

            # Run command and append elapsed time.
            with Timer() as timer:
                result = send_commands([query, 'command=get_rooms'], timeout=20)
            rooms_times.append(timer.elapsed)

            print query
            print 'rooms command {} of {}: time={:1.6f}s, len(result)={}'.format(
                    i+1, num_queries, timer.elapsed, len(result) - 3)

    return rooms_times

def test_simple(rwu_jar_path):
    ''' Run simple test command to ensure DB2 is operational. '''

    with ReserveWithUsAppSession(rwu_jar_path) as session:
        print send_commands(
                ['command=search_rooms&country=country3&numrooms=1&'
                    'date_start=2013-11-05&date_stop=2013-11-09',
                    'command=get_rooms'], timeout=20)

def save_times_to_csv(times, outfile):
    '''
    Save a list of times to CSV.

    Parameters
    ----------
    times : list of float
        Times to save to CSV file.
    outfile : file or path string
        File to write to.
    '''

    if isinstance(outfile, str):
        outfile = open(outfile, 'w')

    csvwriter = csv.writer(outfile)
    csvwriter.writerows([time] for time in times)

def load_times_from_csv(infile):
    '''
    Load a list of times from CSV.

    Parameters
    ----------
    infile : file or path string
        File to read times from.
    '''

    if isinstance(infile, str):
        infile = open(infile, 'r')

    return [float(row[0]) for row in csv.reader(infile)]

