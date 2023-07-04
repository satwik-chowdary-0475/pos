import csv

with open('/Users/satwikchowdary/Desktop/pos/src/main/resources/product.tsv', 'wt') as out_file:
    tsv_writer = csv.writer(out_file, delimiter='\t')
    tsv_writer.writerow(['name', 'barcode','brandCategory','mrp'])
    tsv_writer.writerow(['dairymilk','barcode1','1','100.23'])
    tsv_writer.writerow(['dabur honey', 'barcode2','2','342'])
    tsv_writer.writerow(['air jordan','barcode3','3','10000'])
