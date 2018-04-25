const functions = require('firebase-functions');
const recommendations = require('./recommendations');
const search = require('./search');

exports.generateRecommendations = functions.https.onRequest(
  recommendations.generateRecommendations
);

exports.onShopCreated = functions.database.ref('/shops/{shopId}/').onCreate(
  search.onShopCreate
);

exports.onDrinkCreated = functions.database.ref('/drinks/{drinkId}/').onCreate(
  search.onDrinkCreate
);

exports.onBeanCreated = functions.database.ref('/beans/{beanId}/').onCreate(
  search.onBeanCreate
);

