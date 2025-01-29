import os


def rename_text_files(folder_path):
    # Iterate over all files in the folder
    for file_name in os.listdir(folder_path):
        # Check if the file name contains ".txt"
        if ".txt" in file_name:
            # Remove the first occurrence of ".txt" in the file name
            new_file_name = file_name.replace(".txt", "", 1)  # Replace the first ".txt"

            # Get the full file paths
            old_file_path = os.path.join(folder_path, file_name)
            new_file_path = os.path.join(folder_path, new_file_name)

            # Rename the file
            os.rename(old_file_path, new_file_path)
            print(f"Renamed: {file_name} -> {new_file_name}")


# Example usage
folder_path = "C:/Users/chen4/Documents/GitHub/LLM_script_results-analysis/model_response/crewai/gpt4o"  # Replace with the path to your folder
rename_text_files(folder_path)
