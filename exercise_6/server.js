var http = require("http");
var crypto = require("crypto");
var fs = require("fs").promises;
var host = "localhost";
var port = 5000;
var indexFile;
var sockets = [];
var server = http.createServer(function (req, res) {
    res.setHeader("Content-Type", "text/html");
    res.writeHead(200);
    res.end(indexFile);
});
fs.readFile("".concat(__dirname, "/index.html"))
    .then(function (contents) {
    indexFile = contents;
    server.listen(port, host, function () {
        return console.log("Server is running on http://".concat(host, ":").concat(port));
    });
})
    .catch(function (err) {
    console.error("Could not read index.html file: ".concat(err));
    process.exit(1);
});

server.on("upgrade", function (req, socket) {
    if (req.headers["upgrade"] !== "websocket") {
        socket.end("HTTP/1.1 400 Bad Request");
        return;
    }
    var acceptKey = req.headers["sec-websocket-key"];
    var hash = generateAcceptValue(acceptKey);
    var responseHeaders = [
        "HTTP/1.1 101 Web Socket Protocol Handshake",
        "Upgrade: WebSocket",
        "Connection: Upgrade",
        "Sec-WebSocket-Accept: ".concat(hash),
    ];
    var protocol = req.headers["sec-websocket-protocol"];
    var protocols = protocol ? protocol.split(",").map(function (s) { return s.trim(); }) : [];
    if (protocols.includes("json")) {
        responseHeaders.push("Sec-WebSocket-Protocol: json");
    }
    socket.write(responseHeaders.join("\r\n") + "\r\n\r\n");
    sockets.push(socket);
    try {
      sockets.forEach(function (s, index) {
          var connectedUsers = sockets.map(function (_, i) {
              return "user" + (i + 1);
          }).join(", ");
          return s.write(constructReply({
              connected: connectedUsers,
          }));
      });
    } catch (e) {
        console.log("Error:", e);
    }  
    socket.on("data", function (buffer) {
        try {
            var message_1 = parseMessage(buffer);
            if (message_1) {
                sockets.forEach(function (s) {
                    return s.write(constructReply({
                        message: message_1.message,
                    }));
                });
            }
            else if (message_1 === null) {
                sockets = sockets.filter(function (s) { return s !== socket; });
                sockets.forEach(function (s, index) {
                  var connectedUsers = sockets.map(function (_, i) {
                      return "user" + (i + 1);
                  }).join(", ");
                  return s.write(constructReply({
                      connected: connectedUsers,
                  }));
              });
            }
        }
        catch (e) {
            console.log("Error:", e);
        }
    });
});

var constructReply = function (data) {
    var json = JSON.stringify(data);
    var jsonByteLength = Buffer.byteLength(json);
    var lengthByteCount = jsonByteLength < 126 ? 0 : 2;
    var payloadLength = lengthByteCount === 0 ? jsonByteLength : 126;
    var buffer = Buffer.alloc(2 + lengthByteCount + jsonByteLength);
    buffer.writeUInt8(129, 0);
    buffer.writeUInt8(payloadLength, 1);
    var payloadOffset = 2;
    if (lengthByteCount > 0) {
        buffer.writeUInt16BE(jsonByteLength, 2);
        payloadOffset += lengthByteCount;
    }
    buffer.write(json, payloadOffset);
    return buffer;
};

var parseMessage = function (buffer) {
    var firstByte = buffer.readUInt8(0);
    var opCode = firstByte & 0xf;
    if (opCode === 0x8) {
        return null;
    }
    if (opCode !== 0x1) {
        return;
    }
    var secondByte = buffer.readUInt8(1);
    var isMasked = Boolean((secondByte >>> 7) & 0x1);
    var currentOffset = 2;
    var payloadLength = secondByte & 0x7f;
    if (payloadLength > 125) {
        if (payloadLength === 126) {
            payloadLength = buffer.readUInt16BE(currentOffset);
            currentOffset += 2;
        }
        else {
            throw new Error("Large payloads is not supported");
        }
    }
    var maskingKey;
    if (isMasked) {
        maskingKey = buffer.readUInt32BE(currentOffset);
        currentOffset += 4;
    }
    var data = Buffer.alloc(payloadLength);
    if (isMasked) {
        for (var i = 0, j = 0; i < payloadLength; ++i, j = i % 4) {
            var shift = j == 3 ? 0 : (3 - j) << 3;
            var mask = (shift == 0 ? maskingKey : maskingKey >>> shift) & 0xff;
            var source = buffer.readUInt8(currentOffset++);
            data.writeUInt8(mask ^ source, i);
        }
    }
    else {
        buffer.copy(data, 0, currentOffset++);
    }
    var json = data.toString("utf8");
    return JSON.parse(json);
};

var generateAcceptValue = function (acceptKey) {
    return crypto
        .createHash("sha1")
        .update(acceptKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11", "binary")
        .digest("base64");
};
