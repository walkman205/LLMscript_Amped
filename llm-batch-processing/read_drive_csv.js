import Papa from 'papaparse';
import fetch from 'node-fetch';

/**
 * Fetch the CSV file from a public Google Sheets direct-download link.
 * @param {string} url - The direct-download CSV URL
 * @returns {Promise<string>} - CSV file content as plain text
 */
async function fetchCSVFile(url) {
    const response = await fetch(url);
    if (!response.ok) {
        throw new Error(`Failed to fetch CSV: ${response.status} ${response.statusText}`);
    }
    return await response.text();
}

/**
 * Parse CSV text into an array of objects using PapaParse.
 * @param {string} csvText - Raw CSV text
 * @param {string} delimiter - Delimiter used in the CSV file (default is a comma)
 * @returns {Promise<Array<Object>>} - Array of row objects with keys from the header row
 */
function parseCSV(csvText, delimiter = ",") {
    return new Promise((resolve, reject) => {
        Papa.parse(csvText, {
            header: true,
            delimiter, // Specify the delimiter if it's not a comma
            skipEmptyLines: true,
            complete: (results) => resolve(results.data),
            error: reject,
        });
    });
}

/**
 * Process the CHOPS_items.csv file.
 * Transforms each row into [ itemname, item_text, ground_truth ].
 * @param {string} url - The direct-download CSV URL
 * @returns {Promise<Array<Array<string>>>} - Transformed array of arrays
 */
async function processCHOPSItems(url) {
    const csvText = await fetchCSVFile(url);
    const rows = await parseCSV(csvText);

    // Transform into array of arrays
    const chopsItems = rows.map((row) => [
        row.itemname,
        row.item_text,
        row.ground_truth,
    ]);

    return chopsItems;
}

/**
 * Process the Prompt_snippets.csv file.
 * Transforms each row into [ snippet_name, snippet_text ].
 * @param {string} url - The direct-download CSV URL
 * @returns {Promise<Array<Array<string>>>} - Transformed array of arrays
 */
async function processPromptSnippets(url) {
    const csvText = await fetchCSVFile(url);
    const rows = await parseCSV(csvText);

    // Transform into array of arrays
    const promptSnippets = rows.map((row) => [
        row.snippet_name,
        row.snippet_text,
    ]);

    return promptSnippets;
}

const CHOPS_ITEMS_URL =
    "https://docs.google.com/spreadsheets/d/10X27hD6v5SP5EvbyYb2DE1IG49HMVjOUvAqwHQhfUo0/export?format=csv&gid=0";

const chopsItems = await processCHOPSItems(CHOPS_ITEMS_URL);
export { chopsItems };

const PROMPT_SNIPPETS_URL =
    "https://docs.google.com/spreadsheets/d/1V8urpdL8ZhCYD5PvHmFwwZYRsC-T9Dgj_mwSPHZmeBg/export?format=csv&gid=0";

const promptSnippets = await processPromptSnippets(PROMPT_SNIPPETS_URL);
const promptvarieties = {};

for (const [name, text] of promptSnippets) {
    promptvarieties[name] = text;
}
export { promptvarieties };


//const preprompt = `${promptvarieties.ZSP}${promptvarieties.end_of_prompt_snippet}`;
//console.log(preprompt);