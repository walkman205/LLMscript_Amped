// googleAPI.js
import { GoogleGenerativeAI } from "@google/generative-ai";

const apiKey = process.env.GOOGLE_API_KEY;
console.log("API Key:", apiKey);

if (!apiKey) {
  console.error("Error: GOOGLE_API_KEY environment variable is not set.");
  process.exit(1);
}

const genAI = new GoogleGenerativeAI(apiKey);

// Get the Gemini model
// const model = genAI.getGenerativeModel({ model: 'gemini-1-5-base' }); // Adjust model name as needed
const model = genAI.getGenerativeModel({ model: "gemini-1.5-flash" });

export async function generateContentGoogle(prompt) {
  try {
    // Generate content using the Gemini model
    const result = await model.generateContent(prompt);

    // Extract the generated text
    const text = result.response.text();

    return text;
  } catch (error) {
    console.error(
      "Error generating content with Google Generative AI:",
      error.response?.data || error.message
    );
    throw error;
  }
}
