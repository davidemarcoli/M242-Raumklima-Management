const fs = require('node:fs');
const path = require('node:path');
const { Client, Collection, Events, GatewayIntentBits } = require('discord.js');
const Discord = require("discord.js");

const config = require("./config.json");

const client = new Discord.Client({ intents: [GatewayIntentBits.Guilds] });

client.commands = new Collection();

const commandsPath = path.join(__dirname, 'commands');
const commandFiles = fs.readdirSync(commandsPath).filter(file => file.endsWith('.js'));

for (const file of commandFiles) {
  const filePath = path.join(commandsPath, file);
  const command = require(filePath);
  // Set a new item in the Collection with the key as the command name and the value as the exported module
  if ('data' in command && 'execute' in command) {
    client.commands.set(command.data.name, command);
  } else {
    console.log(`[WARNING] The command at ${filePath} is missing a required "data" or "execute" property.`);
  }
}

client.once("ready", () => {
  console.log(`Ready`);
});

client.on(Events.InteractionCreate, async interaction => {
  if (!interaction.isChatInputCommand()) return;

  const command = interaction.client.commands.get(interaction.commandName);

  if (!command) {
    console.error(`No command matching ${interaction.commandName} was found.`);
    return;
  }

  try {
    await command.execute(interaction);
  } catch (error) {
    console.error(error);
    if (interaction.replied || interaction.deferred) {
      await interaction.followUp({ content: 'There was an error while executing this command!', ephemeral: true });
    } else {
      await interaction.reply({ content: 'There was an error while executing this command!', ephemeral: true });
    }
  }
});

// client.on("message", async (message) => {
//   // console.log(message);
//   // Turns the bot on
//
//   if (message.author.bot) return;
//   // The bot will ignore messages from other bots
//
//   if (!message.content.startsWith(config.prefix)) return;
//   // Ignores messages that don't start with the prefix
//
//   if (message.content == `${config.prefix}ping`) {
//     // Read for the ping command
//     message.channel.send(`Pong!`);
//     // Send 'pong' into the channel if the ping command is heard
//   }
//
//   if (message.content == `${config.prefix}stats`) {
//     // Read for the ping command
//     message.channel.send(
//       "https://server.davidemarcoli.dev/d-solo/a5c0b3cc-81cd-4d5d-98f6-d3f6f4c2dff7/env-iii?orgId=1&from=1687833444932&to=1687855044932&panelId=1"
//     );
//     // Send 'pong' into the channel if the ping command is heard
//   }
// });

client.login(config.token);
