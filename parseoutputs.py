import os
import re
import csv
import sys

def check_file_content(file_path):
    with open(file_path, 'r', encoding='utf-8', errors='ignore') as file:
        content = file.read().lower()  # Convert content to lowercase to match in any case
        has_pass = bool(re.search(r'\bpass\b', content))
        has_fail = bool(re.search(r'\bfail\b', content))
        
        if has_pass and has_fail:
            return "both"
        elif has_pass:
            return "pass"
        elif has_fail:
            return "fail"
        else:
            return "neither"

def scan_files_and_generate_csv(directory):
    # Ensure directory ends with a slash
    directory = os.path.join(directory, '')

    # Filter for .txt files
    files = [f for f in os.listdir(directory) if os.path.isfile(os.path.join(directory, f)) and f.endswith('.txt')]
    results = []

    for file in files:
        file_path = os.path.join(directory, file)
        status = check_file_content(file_path)
        results.append([file, status])

    with open(os.path.join(directory, 'results.csv'), 'w', newline='') as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow(["File Name", "Status"])
        writer.writerows(results)

    print(f"CSV file has been generated with the results in {directory}")

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python script.py <directory_name>")
    else:
        directory_name = sys.argv[1]
        scan_files_and_generate_csv(directory_name)

