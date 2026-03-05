import mimetypes
import os
from google import genai
from google.genai import types
from PIL import Image
import io

# Setup API Key (Provided by user)
os.environ["GEMINI_API_KEY"] = "AIzaSyBlaf0wJCoIcN2BRf5-siSvrmyXE8hHbxk"

client = genai.Client()
model = "gemini-3.1-flash-image-preview"

def generate_image(prompt, aspect_ratio, output_filename, resize_dimensions=None):
    print(f"Generating image for: {output_filename}")
    
    contents = [
        types.Content(
            role="user",
            parts=[types.Part.from_text(text=prompt)],
        ),
    ]
    tools = [
        types.Tool(googleSearch=types.GoogleSearch(
            search_types=types.SearchTypes(web_search=types.WebSearch()),
        )),
    ]
    generate_content_config = types.GenerateContentConfig(
        thinking_config=types.ThinkingConfig(thinking_level="MINIMAL"),
        image_config = types.ImageConfig(
            aspect_ratio=aspect_ratio,
            image_size="1K",
        ),
        response_modalities=["IMAGE"],
        tools=tools,
    )

    try:
        response = client.models.generate_content_stream(
            model=model,
            contents=contents,
            config=generate_content_config,
        )
        for chunk in response:
            if chunk.parts and chunk.parts[0].inline_data and chunk.parts[0].inline_data.data:
                data_buffer = chunk.parts[0].inline_data.data
                
                # Resize if needed
                if resize_dimensions:
                    image = Image.open(io.BytesIO(data_buffer))
                    image = image.resize(resize_dimensions, Image.Resampling.LANCZOS)
                    image.save(output_filename, format="PNG")
                else:
                    with open(output_filename, "wb") as f:
                        f.write(data_buffer)
                print(f"Successfully saved {output_filename}")
                return
    except Exception as e:
        print(f"Error generating {output_filename}: {e}")

if __name__ == "__main__":
    # App Icon (512x512)
    generate_image(
        prompt="A modern, minimalist, flat vector logo for a driving school app in Kenya. It features a stylized steering wheel and an open road with the colors of the Kenyan flag (black, red, green, white). The text 'Dereva Smart Kenya' should be clearly integrated into the design. Clean, flat design, white background. Professional, high quality.",
        aspect_ratio="1:1",
        output_filename="app_icon_512.png",
        resize_dimensions=(512, 512)
    )

    # Feature Graphic (1024x500)
    generate_image(
        prompt="A high-quality, professional promotional feature graphic for a driving school app. It shows a modern car driving on a clean road with beautiful Nairobi skyline or Mount Kenya scenery in the background. The text 'Dereva Smart Kenya' must be prominently and clearly displayed in a bold, readable font. The design is sleek, vibrant, and professional.",
        aspect_ratio="16:9",
        output_filename="feature_graphic_1024x500.png",
        resize_dimensions=(1024, 500)
    )

    # Screenshots (9:16, 1080x1920)
    screenshot_prompts = [
        ("A high-quality mobile app UI screenshot of the 'Dereva Smart Kenya' welcome screen. It shows a sleek, modern login interface for a driving test preparation app. The background is clean with a subtle Kenyan flag theme. Professional Android app design.", "screenshot_1_welcome.png"),
        ("A mobile app UI screenshot of the 'Dereva Smart Kenya' home dashboard. It features colorful cards for 'Mock Tests', 'Learning Modules', '3D Simulation', and 'Progress'. This is a study companion for driving students. Vibrant and modern Material Design 3 style.", "screenshot_2_home.png"),
        ("A mobile app UI screenshot showing an interactive mock test for the driving theory exam in 'Dereva Smart Kenya'. It displays a multiple-choice practice question about road signs with a clear image of a 'No Entry' sign and four options below. Clean and easy to read.", "screenshot_3_mocktest.png"),
        ("A mobile app UI screenshot of a 3D driving simulation lesson in 'Dereva Smart Kenya'. It shows a 3D model town board for practice. On-screen controls for steering and movement are visible. High-quality educational graphics.", "screenshot_4_simulation.png"),
        ("A mobile app UI screenshot of the 'Dereva Smart Kenya' progress tracking screen. It shows beautiful charts and graphs indicating the student's study performance in different categories like 'Road Signs', 'Traffic Rules', and 'Vehicle Safety'. Encouraging and data-driven design.", "screenshot_5_progress.png")
    ]

    for prompt, filename in screenshot_prompts:
        generate_image(
            prompt=prompt,
            aspect_ratio="9:16",
            output_filename=filename,
            resize_dimensions=(1080, 1920)
        )

    # 7-inch Tablet Screenshots (9:16, e.g., 1200x1920)
    tablet_7_prompts = [
        ("A high-quality 7-inch tablet UI screenshot of the 'Dereva Smart Kenya' home dashboard. The interface is optimized for a larger screen with more spacing and clear navigation cards for 'Mock Tests' and '3D Simulation'. Professional study tool design.", "tablet_7in_1_home.png"),
        ("A 7-inch tablet UI screenshot showing a detailed 3D model town board simulation in 'Dereva Smart Kenya'. The larger screen allows for better visibility of the intersection and vehicle controls.", "tablet_7in_2_simulation.png")
    ]

    for prompt, filename in tablet_7_prompts:
        generate_image(
            prompt=prompt,
            aspect_ratio="9:16",
            output_filename=filename,
            resize_dimensions=(1200, 1920)
        )

    # 10-inch Tablet Screenshots (9:16, e.g., 1600x2560)
    tablet_10_prompts = [
        ("A premium 10-inch tablet UI screenshot of the 'Dereva Smart Kenya' learning curriculum. The large screen displays rich text and diagrams for road signs and traffic rules side-by-side. Clean and scholarly aesthetic.", "tablet_10in_1_curriculum.png"),
        ("A 10-inch tablet UI screenshot of a mock driving test in 'Dereva Smart Kenya'. Multiple choice questions are displayed with large, clear images of road situations and signs. Perfect for classroom study.", "tablet_10in_2_mocktest.png")
    ]

    for prompt, filename in tablet_10_prompts:
        generate_image(
            prompt=prompt,
            aspect_ratio="9:16",
            output_filename=filename,
            resize_dimensions=(1600, 2560)
        )
