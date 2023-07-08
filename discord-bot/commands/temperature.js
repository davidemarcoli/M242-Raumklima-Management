const { SlashCommandBuilder } = require('discord.js');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('temperature')
        .setDescription('Get a graph of the temperature of the last 6 hours.'),
    async execute(interaction) {
        // interaction.guild is the object representing the Guild in which the command was run
        const currentTimestamp = Date.now();
        const sixHoursAgo = currentTimestamp - 6 * 60 * 60 * 1000;
        console.log("Current timestamp: " + currentTimestamp)
        console.log("Six hours ago: " + sixHoursAgo)
        console.log(`https://server.davidemarcoli.dev/render/d-solo/a5c0b3cc-81cd-4d5d-98f6-d3f6f4c2dff7/env-iii?orgId=1&from=${sixHoursAgo}&to=${currentTimestamp}&panelId=2&width=1000&height=500&tz=Europe%2FZurich`)
        await interaction.reply({files: [{attachment: `https://server.davidemarcoli.dev/render/d-solo/a5c0b3cc-81cd-4d5d-98f6-d3f6f4c2dff7/env-iii?orgId=1&from=${sixHoursAgo}&to=${currentTimestamp}&panelId=2&width=1000&height=500&tz=Europe%2FZurich`, name: `temperature.png`}]});
    },
};