const express = require('express');

const app = new express();
app.listen(8080);
app.get('/', (req, res) => {
    res.send({
        code: 200,
    });
});