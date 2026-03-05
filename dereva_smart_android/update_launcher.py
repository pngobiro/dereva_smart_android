import os
from PIL import Image

def resize_and_save(source_path, target_base_dir, filename_prefix):
    source_img = Image.open(source_path)
    
    # Mipmap densities and sizes for ic_launcher (48dp base)
    # mdpi: 48, hdpi: 72, xhdpi: 96, xxhdpi: 144, xxxhdpi: 192
    densities = {
        'mipmap-mdpi': 48,
        'mipmap-hdpi': 72,
        'mipmap-xhdpi': 96,
        'mipmap-xxhdpi': 144,
        'mipmap-xxxhdpi': 192
    }
    
    for folder, size in densities.items():
        folder_path = os.path.join(target_base_dir, folder)
        os.makedirs(folder_path, exist_ok=True)
        
        # Save square legacy icon
        img = source_img.resize((size, size), Image.Resampling.LANCZOS)
        img.save(os.path.join(folder_path, f"{filename_prefix}.png"))
        
        # Save round legacy icon (circular crop)
        round_img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
        from PIL import ImageDraw
        mask = Image.new('L', (size, size), 0)
        draw = ImageDraw.Draw(mask)
        draw.ellipse((0, 0, size, size), fill=255)
        round_img.paste(img, (0, 0), mask)
        round_img.save(os.path.join(folder_path, f"{filename_prefix}_round.png"))

def resize_foreground(source_path, target_base_dir):
    source_img = Image.open(source_path)
    
    # Foreground size for adaptive icons is 108dp (center 72dp is safe zone)
    # mdpi: 108, hdpi: 162, xhdpi: 216, xxhdpi: 324, xxxhdpi: 432
    densities = {
        'mipmap-mdpi': 108,
        'mipmap-hdpi': 162,
        'mipmap-xhdpi': 216,
        'mipmap-xxhdpi': 324,
        'mipmap-xxxhdpi': 432
    }
    
    for folder, size in densities.items():
        folder_path = os.path.join(target_base_dir, folder)
        os.makedirs(folder_path, exist_ok=True)
        
        img = source_img.resize((size, size), Image.Resampling.LANCZOS)
        img.save(os.path.join(folder_path, "ic_launcher_foreground.png"))

if __name__ == "__main__":
    res_dir = "/home/ngobiro/projects/Dereva Smart/dereva_smart_android/app/src/main/res"
    
    # Generate legacy icons from the Play Store icon
    resize_and_save("app_icon_512.png", res_dir, "ic_launcher")
    
    # Generate foreground for adaptive icons
    resize_foreground("ic_launcher_foreground_source.png", res_dir)
    
    print("Launcher assets updated successfully.")
