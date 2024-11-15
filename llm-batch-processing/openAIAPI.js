// openAIAPI.js
import { OpenAI } from "openai";

// Initialize the OpenAI client
const openai = new OpenAI({
  apiKey: process.env.OPENAI_API_KEY,
});

export async function generateContentOpenAI(prompt) {
  try {
    // Use the Chat Completion API
    const response = await openai.chat.completions.create({
      model: "gpt-4-turbo", // or 'gpt-4' if you have access
      messages: [{ role: "user", content: prompt }],
      max_tokens: 1000,
      temperature: 0.7,
    });

    // Extract the generated text
    const text = response.choices[0].message.content.trim();

    return text;
  } catch (error) {
    console.error("Error generating content with OpenAI:", error.message);
    throw error;
  }
}
