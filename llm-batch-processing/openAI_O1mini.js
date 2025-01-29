// openAIAPI.js
import { OpenAI } from "openai";

// Initialize the OpenAI client
const openai = new OpenAI({
  apiKey: process.env.OPENAI_API_KEY,
});

export async function generateContentO1Mini(prompt) {
  try {
    const response = await openai.chat.completions.create({
      model: "o1-mini",
      messages: [{ role: "user", content: prompt }],
      max_completion_tokens: 2000,
      temperature: 1,
    });

    const text = response.choices && response.choices.length > 0
        ? response.choices[0].message.content
        : '';
    if (!text) throw new Error("No content generated by the model.");

    return text;
  } catch (error) {
    console.error("Error generating content with OpenAI:", {
      message: error.message,
      stack: error.stack,
      response: error.response?.data || 'No response data',
    });
    throw error;
  }
}
