import os
import re

SCREENS_DIR = r'c:\Users\Kaveesha\Downloads\LankaSmartMartFinal\LankaSmartMart1\LankaSmartMart\app\src\main\java\com\example\lankasmartmart\ui\screens'

COMMON_IMPORTS = [
    'import androidx.compose.runtime.*',
    'import androidx.compose.ui.Modifier',
    'import androidx.compose.ui.Alignment',
    'import androidx.compose.ui.graphics.Color',
    'import androidx.compose.ui.platform.LocalContext',
    'import androidx.compose.material3.*',
    'import androidx.compose.foundation.layout.*',
]

def fix_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    content = ''.join(lines)
    
    # Check if it's a Compose file
    if '@Composable' not in content:
        return
    
    new_imports = []
    for imp in COMMON_IMPORTS:
        # Check if import already exists (literal or wildcard)
        base_pkg = imp.split(' ')[1].replace('.*', '')
        if f'import {base_pkg}' not in content:
            new_imports.append(imp)
    
    if not new_imports:
        return
    
    # Find the last import line or the package line
    last_import_idx = -1
    package_idx = -1
    for i, line in enumerate(lines):
        if line.startswith('import '):
            last_import_idx = i
        elif line.startswith('package '):
            package_idx = i
            
    insert_idx = last_import_idx + 1 if last_import_idx != -1 else package_idx + 1
    
    for imp in reversed(new_imports):
        lines.insert(insert_idx, imp + '\n')
    
    with open(filepath, 'w', encoding='utf-8') as f:
        f.writelines(lines)
    print(f"Fixed imports in {os.path.basename(filepath)}")

if __name__ == "__main__":
    for filename in os.listdir(SCREENS_DIR):
        if filename.endswith('.kt'):
            fix_file(os.path.join(SCREENS_DIR, filename))
