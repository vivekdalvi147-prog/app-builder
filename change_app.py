import os
import sys
import shutil

# CONFIGURATION
OLD_PKG = "com.packag"  # Aapke template ka purana package name
BASE_PATH = "app/src/main"

def main():
    if len(sys.argv) < 3:
        print("Usage: python change_app.py <new_package> <app_name>")
        sys.exit(1)

    NEW_PKG = sys.argv[1]
    APP_NAME = sys.argv[2]

    # 1. MOVE FOLDERS
    old_pkg_path = os.path.join(BASE_PATH, "java", *OLD_PKG.split('.'))
    new_pkg_path = os.path.join(BASE_PATH, "java", *NEW_PKG.split('.'))

    if os.path.exists(old_pkg_path):
        os.makedirs(new_pkg_path, exist_ok=True)
        for filename in os.listdir(old_pkg_path):
            shutil.move(os.path.join(old_pkg_path, filename), os.path.join(new_pkg_path, filename))
        shutil.rmtree(os.path.join(BASE_PATH, "java", OLD_PKG.split('.')[0]))

    # 2. REPLACE TEXT IN FILES
    for root, dirs, files in os.walk("."):
        if ".git" in root or "build" in root: continue
        for file in files:
            if file.endswith(('.java', '.xml', '.gradle')):
                path = os.path.join(root, file)
                try:
                    with open(path, 'r', encoding='utf-8') as f: s = f.read()
                    s = s.replace(OLD_PKG, NEW_PKG)
                    s = s.replace("BolAI", APP_NAME) # "BolAI" ki jagah wo naam likhein jo abhi strings.xml me hai
                    with open(path, 'w', encoding='utf-8') as f: f.write(s)
                except: pass

    # 3. REPLACE ASSETS
    if os.path.exists("user_icon.png"):
        shutil.copy("user_icon.png", f"{BASE_PATH}/res/drawable/app_icon.png")
        shutil.copy("user_icon.png", f"{BASE_PATH}/res/mipmap-hdpi/ic_launcher.png")
    
    if os.path.exists("user_index.html"):
        if not os.path.exists(f"{BASE_PATH}/assets"): os.makedirs(f"{BASE_PATH}/assets")
        shutil.copy("user_index.html", f"{BASE_PATH}/assets/index.html")

if __name__ == "__main__":
    main()
