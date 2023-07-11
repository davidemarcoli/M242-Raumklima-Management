// export default class DateUtils {
//     static parseTimeString(timeString) {
//         const now = new Date();
//         const timestamp = now.getTime();
//
//         const matches = timeString.match(/(\d+)\s*(\w+)/);
//         if (!matches) {
//             return null;
//         }
//
//         const value = parseInt(matches[1], 10);
//         const unit = matches[2].toLowerCase();
//
//         let startTime;
//         switch (unit) {
//             case 'years':
//             case 'year':
//                 startTime = timestamp - value * 365 * 24 * 60 * 60 * 1000;
//                 break;
//             case 'months':
//             case 'month':
//                 startTime = timestamp - value * 30 * 24 * 60 * 60 * 1000;
//                 break;
//             case 'weeks':
//             case 'week':
//                 startTime = timestamp - value * 7 * 24 * 60 * 60 * 1000;
//                 break;
//             case 'days':
//             case 'day':
//                 startTime = timestamp - value * 24 * 60 * 60 * 1000;
//                 break;
//             case 'hours':
//             case 'hour':
//                 startTime = timestamp - value * 60 * 60 * 1000;
//                 break;
//             case 'minutes':
//             case 'minute':
//                 startTime = timestamp - value * 60 * 1000;
//                 break;
//             case 'seconds':
//             case 'second':
//                 startTime = timestamp - value * 1000;
//                 break;
//             default:
//                 return null;
//         }
//
//         return new Date(startTime);
//     }
// }
