// testGoogleAPI.js
import { GoogleGenerativeAI } from "@google/generative-ai";

const apiKey = process.env.GOOGLE_API_KEY;

if (!apiKey) {
  console.error("Error: GOOGLE_API_KEY environment variable is not set.");
  process.exit(1);
}

console.log("API Key:", apiKey);
