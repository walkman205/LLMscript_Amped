const OpenAI = require("openai");
const fs = require("fs");
require("dotenv").config(); // Load environment variables

// Initialize OpenAI client
const openai = new OpenAI({
    apiKey: process.env.OPENAI_API_KEY, // Load API key from .env
});


// Convert an image to base64
function encodeImageToBase64(imagePath) {
    return fs.readFileSync(imagePath, { encoding: "base64" });
}


async function generateAltTextForLocalFiles(imagePaths) {
    for (const imagePath of imagePaths) {

    const imageBase64 = encodeImageToBase64(imagePath);
    const imageUrl = `data:image/png;base64,${imageBase64}`; // Change to "image/jpeg" if it's a JPG
  

    try {
        const response = await openai.chat.completions.create({
          model: "gpt-4o",
          messages: [
            { role: "system", content: "You are an expert in writing concise and descriptive alt-text for images." },
            { role: "user", content: [
                { type: "text", text: "Generate alt-text for the following images for visually-impaired test-takers." },
                { type: "image_url", image_url: { url: imageUrl } } 
              ] 
            }    
          ],
          max_tokens: 100,
        });
    
        console.log(`Alt-Text for ${imagePath}:`, response.choices[0].message.content);
      } catch (error) {
        console.error(`Error processing ${imagePath}:`, error);
      }
    }
}


async function generateAltTextForWebImages(imageUrls) {
    const messages = [
      { role: "system", content: "You are an expert in writing concise and descriptive alt-text for images." },
      { role: "user", content: [
          { type: "text", text: "Generate alt-text for the following images for visually-impaired test-takers." },
          ...imageUrls.map(url => ({
            type: "image_url",
            image_url: { url } // Directly use the web URL instead of encoding
          }))
        ]
      }
    ];
  
    try {
      const response = await openai.chat.completions.create({
        model: "gpt-4o",
        messages: messages,
        max_tokens: 300,
      });
  
      console.log("Generated Alt-Texts:", response.choices[0].message.content);
    } catch (error) {
      console.error("Error generating alt-texts:", error);
    }
  }
  

// Example: Image URLs
const webImageUrls = [
    "http://ampedresearch.xyz/itemimages/T11_G8_M032424_fig1.png",
    "http://ampedresearch.xyz/itemimages/T11_G8_M032424_fig2.png"
];

const imagePaths = [
    "./T11_G8_M032424_fig1.png",
    "./T11_G8_M032424_fig2.png"
];



// Run the function
// generateAltTextForLocalFiles(imagePaths);
// Run the function for web images
generateAltTextForWebImages(webImageUrls);
  