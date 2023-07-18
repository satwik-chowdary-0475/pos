import csv

with open('/Users/satwikchowdary/Desktop/pos/src/main/resources/brand.tsv', 'wt') as out_file:
    tsv_writer = csv.writer(out_file, delimiter='\t')
    tsv_writer.writerow(['brand', 'category'])
    tsv_writer.writerow(['nestle', 'dairy'])
    tsv_writer.writerow(['dabur', 'health'])
    tsv_writer.writerow(['nike', 'shoes'])