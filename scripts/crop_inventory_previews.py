from pathlib import Path
from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "data" / "assets"

# Coordinates are the individual swatch areas in the existing product sheets.
CROPS = {
    # Existing catalog thumbnails, trimmed to the panel surface so web inventory
    # and the mobile app use the same image treatment.
    "01": ("catalog-01.jpg", (0, 0, 185, 155)),
    "84-25": ("catalog-84-25.jpg", (0, 0, 195, 155)),
    "423": ("catalog-423.jpg", (0, 0, 185, 155)),
    "LJS-110": ("catalog-LJS-110.jpg", (0, 0, 190, 120)),
    "LJS-121": ("catalog-LJS-121.jpg", (0, 0, 185, 120)),
    "F-44": ("inventory-seamless2.jpg", (395, 120, 760, 485)),
    "F-213": ("inventory-seamless.jpg", (395, 980, 760, 1345)),
    "F-222": ("inventory-seamless.jpg", (765, 980, 1130, 1345)),
    "PG-00": ("inventory-grove.jpg", (120, 495, 330, 655)),
    "PG-08": ("inventory-grove.jpg", (120, 685, 330, 850)),
    "PG-203": ("inventory-grove.jpg", (345, 1075, 540, 1245)),
    "LJS-102": ("inventory-uvboard.jpg", (925, 960, 1260, 1335)),
    "150-48": ("inventory-5-in-1.jpg", (550, 850, 740, 1010)),
    "150-00B": ("inventory-5-in-1.jpg", (120, 460, 300, 570)),
    "150-10": ("inventory-5-in-1.jpg", (315, 650, 530, 775)),
    "150-05B": ("inventory-5-in-1.jpg", (770, 490, 960, 570)),
    "160-23": ("inventory-4-in-1-fluted.jpg", (770, 520, 1130, 880)),
    "160-KB-01": ("inventory-4-in-1-fluted.jpg", (770, 120, 1130, 480)),
    "160-KB-02": ("inventory-4-in-1-fluted.jpg", (410, 120, 770, 480)),
    "195-04": ("inventory-3-in-1.jpg", (45, 120, 400, 480)),
    "195-05": ("inventory-3-in-1.jpg", (410, 120, 765, 480)),
    "195-07": ("inventory-3-in-1.jpg", (45, 520, 400, 880)),
}

for code, (source_name, box) in CROPS.items():
    source = ASSETS / source_name
    output = ASSETS / f"inventory-crop-{code}.jpg"
    with Image.open(source) as image:
        cropped = image.crop(box)
        # The supplied inventory sheets frame each swatch with an orange/black
        # border. Keep only the panel material in the customer preview asset.
        if source_name not in {
            "inventory-5-in-1.jpg",
            "catalog-01.jpg",
            "catalog-84-25.jpg",
            "catalog-423.jpg",
            "catalog-LJS-110.jpg",
            "catalog-LJS-121.jpg",
        }:
            inset_x = 46
            inset_y = 60
            width, height = cropped.size
            cropped = cropped.crop((inset_x, inset_y, width - inset_x, height - inset_y))
        cropped.save(output, quality=94, optimize=True)
    print(output.name)
