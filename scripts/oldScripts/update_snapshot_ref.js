const https = require('https');
const fs = require('fs');

const options = {
  hostname: 'registry.npmjs.org',
  port: 443,
  path: '/fh-forms-handler',
  method: 'GET',
  headers: {
    'Content-Type': 'application/json',
  }
}



const getFhVersion = async () => {
  const req = () => new Promise((resolve, reject) => {
    https.get('https://registry.npmjs.org/fh-forms-handler', (resp) => {
      let data = '';

      // A chunk of data has been received.
      resp.on('data', (chunk) => {
        data += chunk;
      });

      // The whole response has been received. Print out the result.
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

(async () => {
  let json_version = JSON.parse(fs.readFileSync('./scripts/package.json')).dependencies['fh-forms-handler'];
  const remoteData = (await getFhVersion()).versions;
  let version = Object.keys(remoteData);
  if (json_version.split('-').length === 3) {
    const parts = json_version.split('-');
    parts.pop();
    json_version = parts.join('-');
  }
  version = version.filter(el => el.startsWith(json_version) && el.split('-').length === 3);
  version = version.map(el => {
    const l = el.split('-');
    return Number(l[l.length - 1]);
  });
  const latest = Math.max(...version);
  const latest_ver = `${json_version}-${latest}`
  console.log(latest_ver);
})();