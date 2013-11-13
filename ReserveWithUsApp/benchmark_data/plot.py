'''
Plot room and hotel query results. This is intended to be run in
adbs-reservewithus/ReserveWithUsApp/benchmark_data.
'''

import matplotlib.pyplot as plt
import sys
sys.path.append('../')
import benchmark_queries as bq
import math
import numpy as np

GRID_SIZE = 5.5

# Get CSVs.
raw_data = (
		('Distribution of hotel query times.',
			(
				('NO OPTIMIZATION',
					'hotel_times_no_opt.csv'),
				('index on HOTEL.(COUNTRY,CITY)',
					'hotel_times_hotel_index.csv'),
				('indexes on\n'
					'HOTEL.COUNTRY+CITY, ROOM_DATE.SINGLE_DAY_DATE',
					'hotel_times_hotel_room_date_indexes.csv'),
				)
			),
		('Distribution of room query times.',
			(
				('NO OPTIMIZATION',
					'room_times_no_opt.csv'),
				('index on HOTEL.COUNTRY+CITY',
					'room_times_hotel_index.csv'),
				('indexes on\n'
					'HOTEL.COUNTRY+CITY, ROOM_DATE.SINGLE_DAY_DATE',
					'room_times_hotel_room_date_indexes.csv'),
				('inverted query',
					'room_times_inside_out_only.csv'),
				('inverted query and indexes on\n'
					'HOTEL.COUNTRY+CITY, ROOM_DATE.SINGLE_DAY_DATE',
					'room_times_new_query_indexes_all.csv'),
				)
			)
		)

for plot_idx, (plot_title, plot_grp) in enumerate(raw_data):

	grp_data = [(title, bq.load_times_from_csv(filename)[5:], filename)
			for title, filename in plot_grp]
	max_data = max(max(data) for _, data, _ in grp_data)
	bins_max = int(math.ceil(max_data * 100.0)) / 100.0

	num_axes = len(plot_grp)
	num_cols = int(math.ceil(math.sqrt(num_axes)))
	num_rows = int((num_axes + num_cols - 1.0) / num_cols)

	f = plt.figure(figsize=(GRID_SIZE * num_cols, GRID_SIZE * num_rows))
	f.suptitle(plot_title, fontsize=12)
	for idx, (title, data, filename) in enumerate(grp_data):
		ax = f.add_subplot(num_rows, num_cols, idx)
		ax.hist(data, bins=np.linspace(0., bins_max, num=101))
		ax.set_title(title, fontsize=8)
		ax.set_ylabel('freq')
		ax.set_xlabel('time (s)')
	plt.savefig('{}.png'.format(plot_idx))

