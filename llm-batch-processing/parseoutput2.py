#!/usr/bin/env python3

import sys
import os
import csv

def main():
    if len(sys.argv) < 2:
        print("Usage: python process_txt_files.py <directory>")
        sys.exit(1)

    input_dir = sys.argv[1]

    # Prepare the CSV file
    summarypath = os.path.join(input_dir, "summary.csv")
    with open(summarypath, mode="w", newline="", encoding="utf-8") as csvfile:
        writer = csv.writer(csvfile)
        # Write the header row
        writer.writerow(["item_name", "item_type", "ground_truth", "output_verdict"])

        # Loop over all files in the specified directory
        for filename in sorted(os.listdir(input_dir)):
            # Check if file ends with .txt
            if filename.endswith(".txt"):
                filepath = os.path.join(input_dir, filename)

                # Determine groundtruth
                # (pass if "_good_" in filename, fail if "_bad_" in filename, otherwise ignore)
                if "_good_" in filename:
                    ground_truth = "pass"
                elif "_bad_" in filename:
                    ground_truth = "fail"
                else:
                    continue

                # Determine itemtype
                if "jigsaw" in filename:
                    item_type = "jigsaw"
                elif "joint" in filename:
                    item_type = "jointconstruct"
                elif "request" in filename:
                    item_type = "requestinfo"
                else:
                    continue


                # Read file content and determine output_verdict
                with open(filepath, "r", encoding="utf-8") as f:
                    content = f.read().lower()  # Convert to lowercase to ignore case
                    content = content.replace("*", "") # strip out asterisks

                if "verdict: pass" in content:
                    output_verdict = "pass"
                elif "verdict: fail" in content:
                    output_verdict = "fail"
                else:
                    output_verdict = "unknown"

                # Derive item_name by trimming off any part after "_good" or "_bad"
                # Example: "foo_good_123.txt" => item_name = "foo_good"
                #          "bar_bad_something.txt" => item_name = "bar_bad"
                # We'll find the index of "_good" or "_bad" and take the substring up to that
                # plus 5 characters for "_good" or 4 for "_bad" if needed
                if "_good" in filename:
                    idx = filename.index("_good")
                    item_name = filename[: idx + len("_good")]
                elif "_bad" in filename:
                    idx = filename.index("_bad")
                    item_name = filename[: idx + len("_bad")]
                else:
                    # This case shouldn't happen if we continue to skip unrecognized filenames,
                    # but let's handle it just in case
                    item_name = os.path.splitext(filename)[0]

                # Write row to CSV
                writer.writerow([item_name, item_type, ground_truth, output_verdict])

    print("Summary written to summary.csv")

if __name__ == "__main__":
    main()
