const functions = require('firebase-functions');
const algoliasearch = require('algoliasearch');

const ALGOLIA_ID = functions.config().algolia.app_id;
const ALGOLIA_ADMIN_KEY = functions.config().algolia.api_key;

const client = algoliasearch(ALGOLIA_ID, ALGOLIA_ADMIN_KEY);

const search = {};

search.onShopCreate = (snapshot, context) => {
  const shop = snapshot.val();

  shop.objectId = context.params.shopId;

  const index = client.initIndex('shops');
  return index.saveObject(shop);
};

search.onDrinkCreate = (snapshot, context) => {
  const drink = snapshot.val();

  shop.objectId = context.params.drinkId;

  const index = client.initIndex('drinks');
  return index.saveObject(drink);
};

search.onBeanCreate = (snapshot, context) => {
  const bean = snapshot.val();

  shop.objectId = context.params.beanId;

  const index = client.initIndex('beans');
  return index.saveObject(bean);
};


module.exports = search;
