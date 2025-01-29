import os


def fix_file_extensions(folder_path):
    # Iterate over all files in the folder
    for filename in os.listdir(folder_path):
        file_path = os.path.join(folder_path, filename)

        # Ensure it's a file (not a directory)
        if os.path.isfile(file_path):
            # Check if '.txt' is misplaced in the filename
            if '.txt' in filename:
                # Replace '_txt' with '.txt' at the end
                new_filename = filename.replace('.txt', '') + '.txt'
                new_file_path = os.path.join(folder_path, new_filename)

                # Rename the file
                os.rename(file_path, new_file_path)
                print(f'Renamed: {filename} -> {new_filename}')
            else:
                print(f'Skipped: {filename} (No changes needed)')
    print("File renaming complete.")


# Set the folder path where your files are located
folder_path = 'C:/Users/chen4/Documents/GitHub/LLMscript_Amped/llm-batch-processing/LLAMA'

# Call the function
fix_file_extensions(folder_path)
