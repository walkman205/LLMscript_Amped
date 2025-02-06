// openAIAPI.js
import { OpenAI } from "openai";
import { API_CONFIG } from "./config.js";
const modelName = API_CONFIG.llamaAPImodel;

// Initialize the OpenAI client
const client = new OpenAI({
  apiKey: process.env.LLAMA_API_KEY,
  baseURL: "https://api.llama-api.com",
});

export async function generateContentLLAMA(prompt) {
  try {
    // Use the Chat Completion API
    const response = await client.chat.completions.create({
      model: modelName,
      messages: [{ role: "user", content: prompt }],
      max_tokens: 4000,
      temperature: 0.7,
    });

    // Extract the generated text
    const text = response.choices[0].message.content.trim();

    return text;
  } catch (error) {
    console.error("Error generating content with LLAMA:", error.message);
    throw error;
  }
}
