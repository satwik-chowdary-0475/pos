import csv

with open("/Users/satwikchowdary/Desktop/pos-app/pos/tsv-uploads/inventory.tsv", 'wt') as out_file:
    tsv_writer = csv.writer(out_file, delimiter='\t')
    tsv_writer.writerow(['barcode', 'quantity'])
    for i in range(1,5001):
        tsv_writer.writerow(['barcode'+str(i),i]);
    # tsv_writer.writerow(['barcode1', '100'])
    # tsv_writer.writerow(['barcode2', '1000'])
    # tsv_writer.writerow(['barcode3','10000'])
