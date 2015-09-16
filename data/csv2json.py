import csv
import json

csvfile = open('CTDB.csv', 'r')
jsonfile = open('CTDB.json', 'w')

with open('CTDB.csv', 'r') as csvfile, open('CTDB.json', 'w') as jsonfile:
	reader = csv.DictReader(csvfile)
	header = [s.lower() for s in reader.fieldnames]
	reader = csv.DictReader(csvfile, header)
	json.dump([row for row in reader], jsonfile)