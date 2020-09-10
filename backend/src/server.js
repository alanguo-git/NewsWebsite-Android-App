const express = require('express');
const googleTrends = require('google-trends-api');

var GuaHomeUrl = 'https://content.guardianapis.com/search?order-by=newest&show-fields=starRating,headline,thumbnail,short-url&api-key=YourApiKey';
var GuaWorldUrl = 'https://content.guardianapis.com/world?api-key=YourApiKey&show-blocks=all';
var GuaPoliticsUrl = 'https://content.guardianapis.com/politics?api-key=YourApiKey&show-blocks=all';
var GuaBusinessUrl = 'https://content.guardianapis.com/business?api-key=YourApiKey&show-blocks=all';
var GuaTechnologyUrl = 'https://content.guardianapis.com/technology?api-key=YourApiKey&show-blocks=all';
var GuaSportUrl = 'https://content.guardianapis.com/sport?api-key=YourApiKey&show-blocks=all';
var GuaScienceUrl = 'http://content.guardianapis.com/science?api-key=YourApiKey&show-blocks=all';

var cors = require('cors');
var app = express();
app.use(cors());

const fetch = require('node-fetch');

app.get('/Home', async (req, res) => {
    const fetch_response = await fetch(GuaHomeUrl);
    const json = await fetch_response.json();
    res.send(json);
});
app.get('/World', async (req, res) => {
    const fetch_response = await fetch(GuaWorldUrl);
    const json = await fetch_response.json();
    res.send(json);
});
app.get('/Politics', async (req, res) => {
    const fetch_response = await fetch(GuaPoliticsUrl);
    const json = await fetch_response.json();
    res.send(json);
});
app.get('/Business', async (req, res) => {
    const fetch_response = await fetch(GuaBusinessUrl);
    const json = await fetch_response.json();
    res.send(json);
});
app.get('/Technology', async (req, res) => {
    const fetch_response = await fetch(GuaTechnologyUrl);
    const json = await fetch_response.json();
    res.send(json);
});
app.get('/Sport', async (req, res) => {
    const fetch_response = await fetch(GuaSportUrl);
    const json = await fetch_response.json();
    res.send(json);
});
app.get('/Science', async (req, res) => {
    const fetch_response = await fetch(GuaScienceUrl);
    const json = await fetch_response.json();
    res.send(json);
});
app.get('/Article', async (req, res) => {
    const url = `https://content.guardianapis.com/${req.query.id}?api-key=YourApiKey&show-blocks=all`;
    const fetch_response = await fetch(url);
    const json = await fetch_response.json();
    res.send(json);
});
app.get('/Search', async (req, res) => {
    const url = `https://content.guardianapis.com/search?q=${req.query.keyword}&api-key=YourApiKey&show-blocks=all`;
    const fetch_response = await fetch(url);
    const json = await fetch_response.json();
    res.send(json);
});
app.get('/google-trends', (req, res) => {
    googleTrends.interestOverTime({keyword: req.query.term, startTime: new Date('2019-06-01')})
    .then(function(results){
        res.send(results);
    })
    .catch(function(err){
        res.send(err);
    });
});

var port = process.env.PORT || 3000
app.listen(port);