// anthropicAPI.js
import Anthropic from "@anthropic-ai/sdk";

const anthropic = new Anthropic();

export async function generateContentAnthropic(prompt) {
  const response = await anthropic.messages.create({
    model: "claude-3-5-sonnet-20241022",
    max_tokens: 1000,
    temperature: 0,
    system: "You are an analytical AI.",
    messages: [
      {
        role: "user",
        content: [
          {
            type: "text",
            text: prompt,
          },
        ],
      },
    ],
  });
  return response.content[0].text;
}
