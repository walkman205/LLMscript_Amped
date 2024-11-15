// fileUtils.js
import fs from "fs/promises";
import path from "path";

export async function getFiles(directory) {
  const dirPath = path.resolve(directory);
  return await fs.readdir(dirPath);
}

export async function readFileContent(directory, fileName) {
  const filePath = path.resolve(directory, fileName);
  return await fs.readFile(filePath, "utf8");
}

export async function writeFileContent(directory, fileName, content) {
  const dirPath = path.resolve(directory);
  await fs.mkdir(dirPath, { recursive: true });
  const filePath = path.join(dirPath, fileName);
  await fs.writeFile(filePath, content, "utf8");
}
