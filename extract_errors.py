import re
import sys

def extract_errors(file_path):
    try:
        with open(file_path, 'r', encoding='utf-16') as f:
            for line in f:
                # Look for Kotlin compiler error format
                # e: file:///C:/Users/.../com/example/lankasmartmart/ui/screens/PersonalInfoScreen.kt:37:37: Unresolved reference: extractName
                m = re.search(r'file:///.*?/(com/example/lankasmartmart/.*?):(\d+):(\d+): (.*)', line)
                if m:
                    file_path = m.group(1)
                    line_num = m.group(2)
                    col_num = m.group(3)
                    message = m.group(4)
                    print(f"{file_path}:{line_num} - {message}")
                elif 'e: ' in line and 'error' in line.lower():
                    # Catch other errors that might not match the specific regex
                    print(line.strip())
    except Exception as e:
        print(f"Error reading file: {e}")

if __name__ == '__main__':
    extract_errors('build_errors.txt')
