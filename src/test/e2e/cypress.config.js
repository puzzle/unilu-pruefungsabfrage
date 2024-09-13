const fs = require('fs');
const path = require('path');

module.exports = {
  e2e: {
    setupNodeEvents(on, config) {
      on('task', {
        clearDownloads() {
          const downloadsFolder = path.join(__dirname, 'cypress/downloads');

          fs.readdir(downloadsFolder, (err, files) => {
            if (err) {
              console.log('Could not list the directory.', err);
              return null;
            }

            files.forEach((file) => {
              const filePath = path.join(downloadsFolder, file);
              fs.unlink(filePath, (unlinkErr) => {
                if (unlinkErr) {
                  console.log('Error deleting file:', unlinkErr);
                }
              });
            });
          });

          return null;
        },
      });
    },
    baseUrl: 'http://localhost:8080',
    downloadsFolder: 'cypress/downloads',
  },
};
