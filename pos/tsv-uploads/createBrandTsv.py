import csv

with open('/Users/satwikchowdary/Desktop/pos-app/pos/tsv-uploads/brand.tsv', 'wt') as out_file:
    tsv_writer = csv.writer(out_file, delimiter='\t')
    tsv_writer.writerow(['brand', 'category'])
    for i in range(1,5001):
        tsv_writer.writerow([i,i])


    # tsv_writer.writerow(['nestle', 'dairy'])
    # tsv_writer.writerow(['dabur', 'health'])
    # tsv_writer.writerow(['nike', 'shoes'])