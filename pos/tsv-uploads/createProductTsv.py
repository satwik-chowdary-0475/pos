import csv

with open('/product.tsv', 'wt') as out_file:
    tsv_writer = csv.writer(out_file, delimiter='\t')
    tsv_writer.writerow(['name', 'barcode','brand','category','mrp','x'])
    tsv_writer.writerow(['iphone X','barcode1','apple','mobile','100000'])
    tsv_writer.writerow(['NIke Air',    'barcode2',    'nike' , '  shoes' ,  '5000'])
    tsv_writer.writerow(['dabur honey', 'barcode3' ,  ' nestle' , 'food'  ,  '100.23'])
