import { API_CONFIG } from "./config.js";

import fetch from 'node-fetch';

const modelName = API_CONFIG.localModelName;

export async function generateContentLocal(prompt) {
    try {
      const requestBody = {
        model: "deepseek-r1-distill-qwen-32b",
        messages: [
          { role: "user", content: prompt }
        ],
        temperature: 0.7,
        max_tokens: 7000,  // -1 sometimes indicates 'no limit' in some local LLMs
        stream: false
      };
  
      // Send the POST request to your local LLM endpoint
      const response = await fetch("http://127.0.0.1:1234/v1/chat/completions", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(requestBody)
      });
  
      // Check for HTTP errors
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status} - ${response.statusText}`);
      }
  
      // Parse the JSON response
      const data = await response.json();
  
      const text = data.choices[0].message.content;
      return text;
    } catch (error) {
      console.error("Error generating content with local LLM:", error);
      throw error;
    }
  }
  

