const Discord = require("discord.js");

const config = require("./config.json");

const client = new Discord.Client();

client.once("ready", () => {
  console.log(`Ready`);
});

client.on("message", async (message) => {
  // console.log(message);
  // Turns the bot on

  if (message.author.bot) return;
  // The bot will ignore messages from other bots

  if (!message.content.startsWith(config.prefix)) return;
  // Ignores messages that don't start with the prefix

  if (message.content == `${config.prefix}ping`) {
    // Read for the ping command
    message.channel.send(`Pong!`);
    // Send 'pong' into the channel if the ping command is heard
  }

  if (message.content == `${config.prefix}stats`) {
    // Read for the ping command
    message.channel.send(
      "https://server.davidemarcoli.dev/d-solo/a5c0b3cc-81cd-4d5d-98f6-d3f6f4c2dff7/env-iii?orgId=1&from=1687833444932&to=1687855044932&panelId=1"
    );
    // Send 'pong' into the channel if the ping command is heard
  }
});

client.login("");
