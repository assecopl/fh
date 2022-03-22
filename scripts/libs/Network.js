const https = require('https');
const http = require('http');
const fs = require('fs');

const _prepareFullAddress = (address, params) => {
  if (params) {
    const fullParams = Object.entries(params).map(([key, value]) => `${key}=${value}`).join('&');
    return `${address}?${fullParams}`;
  }
  return address;
}

const get = async ({address, params, headers, protocol = 'https'}) => {
  const connection = protocol === 'https' ? https : http;
  const fullAddress = _prepareFullAddress(address, params);
  const req = () => new Promise((resolve, reject) => {
    connection.get(fullAddress, {headers}, (resp) => {
      let data = '';

      resp.on('data', (chunk) => {
        data += chunk;
      });

      resp.on('end', () => {
        resolve(JSON.parse(data));
      });
    }).on("error", (err) => {
      reject(err);
    });
  });
  const res = await req();
  return res;
}


const post = async ({address, params, headers, body, protocol = 'https'}) => {
  const connection = protocol === 'https' ? https : http;
  const fullAddress = _prepareFullAddress(address, params);
  const req = () => new Promise((resolve, reject) => {
    connection.post(fullAddress, {headers, body}, (resp) => {
      let data = '';

      resp.on('data', (chunk) => {
        data += chunk;
      });

      resp.on('end', () => {
        resolve(JSON.parse(data));
      });
    }).on("error", (err) => {
      reject(err);
    });
  });
  const res = await req();
  return res;
}

const download = (address, dest, name, protocol = 'https') => {
  const connection = protocol === 'https' ? https : http;
  const file = fs.createWriteStream(`${dest}/${name}`);
  const req = () => new Promise((resolve, reject) => {
    connection.get(address, (resp) => {
      let cur = 0;
      const len = parseInt(resp.headers['content-length'], 10);

      file.on('finish', function() {
        file.close(() => {
          console.log('\n')
          resolve();
        });
      });

      resp.on('data', (chunk) => {
        cur += chunk.length;
        const platform = require('os').platform();
        const percentage = ((cur * 100) / len).toFixed(2);
        process.stdout.write((platform == 'win32') ? "\033[0G": "\r");
        process.stdout.write(percentage + "% | " + cur + " bytes downloaded out of " + len + " bytes.");
      }).pipe(file);

    }).on("error", (err) => {
      reject(err);
    });
  });
  return req();
}

module.exports = {get, post, download};