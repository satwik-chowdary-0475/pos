import csv

with open('/Users/satwikchowdary/Desktop/pos-app/pos/src/main/resources/product.tsv', 'wt') as out_file:
    tsv_writer = csv.writer(out_file, delimiter='\t')
    tsv_writer.writerow(['name', 'barcode','brand','category','mrp'])
    tsv_writer.writerow(['dairymilk','barcode1','a','a','100.23'])
    tsv_writer.writerow(['dabur honey', 'barcode2','a','a','342'])
    tsv_writer.writerow(['air jordan','barcode3','b','c','10000'])
