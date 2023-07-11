const { SlashCommandBuilder } = require('discord.js');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('humidity')
        .setDescription('Get a graph of the humidity in the room.')
        .addStringOption(option =>
            option.setName('range').setDescription('The range of the graph.')
        ),
    async execute(interaction) {
        // interaction.guild is the object representing the Guild in which the command was run
        const currentTimestamp = Date.now();

        let startTimestamp = currentTimestamp - 6 * 60 * 60 * 1000;

        await interaction.deferReply();

        console.log(interaction.options.getString('range'))
        if (interaction.options.getString('range')) {
            startTimestamp = parseTimeString(interaction.options.getString('range')).getTime();
        }

        console.log("Current timestamp: " + currentTimestamp)
        console.log("Six hours ago: " + startTimestamp)
        console.log(`https://server.davidemarcoli.dev/render/d-solo/a5c0b3cc-81cd-4d5d-98f6-d3f6f4c2dff7/env-iii?orgId=1&from=${startTimestamp}&to=${currentTimestamp}&panelId=2&width=1000&height=500&tz=Europe%2FZurich`)
        await interaction.editReply({files: [{attachment: `https://server.davidemarcoli.dev/render/d-solo/a5c0b3cc-81cd-4d5d-98f6-d3f6f4c2dff7/env-iii?orgId=1&from=${startTimestamp}&to=${currentTimestamp}&panelId=2&width=1000&height=500&tz=Europe%2FZurich`, name: `temperature.png`}]});
    },
};

function parseTimeString(timeString) {
    const now = new Date();
    const timestamp = now.getTime();

    // const matches = timeString.match(/(\d+)\s*(\w+)/);
    // if (!matches) {
    //     return null;
    // }
    //
    // const value = parseInt(matches[1], 10);
    // const unit = matches[2].toLowerCase();

    const matches = timeString.match(/last\s*(\d+)?\s*(\w+)/);
    if (!matches) {
        return null;
    }

    let value = 1; // Default value is 1 if not specified
    if (matches[1]) {
        value = parseInt(matches[1], 10);
    }
    const unit = matches[2].toLowerCase();

    let startTime;
    switch (unit) {
        case 'years':
        case 'year':
            startTime = timestamp - value * 365 * 24 * 60 * 60 * 1000;
            break;
        case 'months':
        case 'month':
            startTime = timestamp - value * 30 * 24 * 60 * 60 * 1000;
            break;
        case 'weeks':
        case 'week':
            startTime = timestamp - value * 7 * 24 * 60 * 60 * 1000;
            break;
        case 'days':
        case 'day':
            startTime = timestamp - value * 24 * 60 * 60 * 1000;
            break;
        case 'hours':
        case 'hour':
            startTime = timestamp - value * 60 * 60 * 1000;
            break;
        case 'minutes':
        case 'minute':
            startTime = timestamp - value * 60 * 1000;
            break;
        case 'seconds':
        case 'second':
            startTime = timestamp - value * 1000;
            break;
        default:
            return null;
    }

    return new Date(startTime);
}