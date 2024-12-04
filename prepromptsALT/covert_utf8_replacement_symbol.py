import os

def encode_files_to_utf8_and_remove_nbsp_replacement(directory_path):
    files_with_nbsp = []  # List to store names of files where 0xa0 bytes were found and removed
    error_files = []  # List to store names of files that caused errors during processing
    files_with_replacement_char = []  # List to store names of files with the replacement character

    for filename in os.listdir(directory_path):
        if filename.endswith(".txt"):
            file_path = os.path.join(directory_path, filename)
            try:
                with open(file_path, 'rb') as file:
                    content_bytes = file.read()

                # Attempt to replace 0xa0 bytes directly in the byte stream
                content_bytes = content_bytes.replace(b'\xa0', b' ')

                try:
                    # Attempt to decode the modified byte stream with UTF-8
                    content = content_bytes.decode('utf-8')
                except UnicodeDecodeError:
                    # If decoding fails, attempt a more tolerant decoding approach or log the error
                    print(f"Warning: Decoding issue with '{filename}', attempting with 'ignore' strategy.")
                    content = content_bytes.decode('utf-8', errors='ignore')
                    error_files.append(filename)

                if '�' in content:
                    files_with_replacement_char.append(filename)
                    # Remove the replacement character
                    content = content.replace('�', '')

                # Write the content back to the file in UTF-8
                with open(file_path, 'w', encoding='utf-8') as file:
                    file.write(content)

                if filename not in error_files:
                    files_with_nbsp.append(filename)

            except Exception as e:
                print(f"Error processing {filename}: {e}")

    # Report files from which 0xa0 bytes were removed
    if files_with_nbsp:
        print("Files processed successfully:")
        for file in files_with_nbsp:
            print(file)

    # Report files that had decoding issues
    if error_files:
        print("Files with decoding issues (processed with 'ignore' strategy):")
        for file in error_files:
            print(file)

    # After processing all files, print the list of files that had the replacement character
    if files_with_replacement_char:
        print("Files where the replacement character was removed:")
        for file in files_with_replacement_char:
            print(file)
    else:
        print("No replacement characters encountered.")


# Use os.getcwd() to get the current working directory
directory_path = os.getcwd()
encode_files_to_utf8_and_remove_nbsp_replacement(directory_path)