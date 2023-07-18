import csv

with open('/Users/satwikchowdary/Desktop/pos/src/main/resources/inventory.tsv', 'wt') as out_file:
    tsv_writer = csv.writer(out_file, delimiter='\t')
    tsv_writer.writerow(['barcode', 'quantity'])
    tsv_writer.writerow(['barcode1', '100'])
    tsv_writer.writerow(['barcode2', '1000'])
    tsv_writer.writerow(['barcode3','10000'])
