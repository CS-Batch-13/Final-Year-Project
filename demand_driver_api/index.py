from flask import Flask, jsonify, request
import requests
import json
from textblob import TextBlob
import tweepy
import urllib.request
from bs4 import BeautifulSoup
import os.path
import summarize
from flask_cors import CORS, cross_origin
import csv

config = json.loads(open('config.json').read())
app = Flask(__name__)
CORS(app)
sources_arr = config['SOURCES']
sources_string = ""
for source in sources_arr:
    sources_string+=","+source
sources_string = sources_string[1:]
russia_proxy = config['HTTPS_PROXY']
proxy_dict = { "https": russia_proxy }
commodities_names = config["COMMODITIES"]
websol_api_key = config["WEBSOL_API_KEY"]
websol_fields = config["WEBSOL_FIELDS"]
exports = config["EXPORTS"]
imports = config["IMPORTS"]
parameter_hashtags = config["PARAMETERS_HASHTAGS"]

auth = tweepy.OAuthHandler(config["CONSUMER_KEY"], config["CONSUMER_SECRET"])
auth.set_access_token(config["ACCESS_TOKEN"], config["ACCESS_TOKEN_SECRET"])
twitter_extractor = tweepy.API(auth)

@app.route('/api/parameters')
def fetch_parameters():
    parameters = config['PARAMETERS']
    response = { 'parameters': parameters }
    return jsonify(response)

@app.route('/api/headlines')
def fetch_headlines():
    parameters = config['PARAMETERS']
    response_data = []
    for parameter in parameters:
        request_url = "https://newsapi.org/v2/top-headlines?category=business&pageSize=100&apiKey={}&q={}&lang=en".format(config['NEWS_API_KEY'], parameter)
        network_request = requests.get(url=request_url)
        data = network_request.json()
        response_data.append(data)
    return jsonify(response_data)

@app.route('/api/everything')
def fetch_everything():
    parameters = config['PARAMETERS']
    response_data = []
    for parameter in parameters:
        request_url = "https://newsapi.org/v2/everything?pageSize=100&apiKey={}&q={}&language=en".format(sources_string, config['NEWS_API_KEY'], parameter)
        network_request = requests.get(url=request_url)
        data = network_request.json()
        response_data.append(data)
    return jsonify(response_data)

@app.route('/api/query/')
def fetch_query():
    query = request.args["query"]
    request_url = "https://newsapi.org/v2/top-headlines?category=business&apiKey={}&q={}".format(config['NEWS_API_KEY'], query)
    network_request = requests.get(url=request_url)
    data = network_request.json()
    return jsonify(data)

@app.route('/api/tweets')
def fetch_tweets():
    query = request.args["query"]
    wiki = TextBlob(query)
    nouns = wiki.noun_phrases
    alltweets = twitter_extractor.search(q=str(nouns[0]+nouns[1]),count=10)
    tweets = []
    for tweet in alltweets:
        tweet = tweet._json
        tweets.append(tweet)
    return jsonify({ 'tweets': tweets })

@app.route('/api/sentiment/tweets')
def perform_sentiment_analysis():
    query = request.args["query"]
    wiki = TextBlob(query)
    nouns = wiki.noun_phrases
    subjectivity_positive = 0
    subjectivity_negative = 0
    subjectivty_neutral = 0
    polarity_positive = 0
    polarity_negative = 0
    polarity_neutral = 0
    count = 0
    if len(nouns)==0:
        nouns = [ query.split(" ")[len(query.split(" "))-1] ]
    for noun in nouns:
        alltweets = twitter_extractor.search(q=str(noun),count=len(nouns)*10)
        for tweet in alltweets:
            tweet = tweet._json
            wiki = TextBlob(tweet["text"])
            subjectivity = wiki.sentiment.subjectivity
            polarity = wiki.sentiment.polarity
            count+=1

            if polarity>0:
                polarity_positive+=1
            elif polarity<0:
                polarity_negative+=1
            elif polarity==0:
                polarity_neutral+=1
            
            if subjectivity>0:
                subjectivity_positive+=1
            elif subjectivity<0:
                subjectivity_negative+=1
            elif subjectivity==0:
                subjectivty_neutral+=1
    return jsonify({ "nouns":list(nouns), "count":count, "subjectivity_positive": subjectivity_positive, "subjectivity_negative": subjectivity_negative, "subjectivity_neutral": subjectivty_neutral, "polarity_positive": polarity_positive, "polarity_negative": polarity_negative, "polarity_neutral": polarity_neutral })

@app.route('/api/summary')
def fetch_summary():
    query = request.args["query"]
    summary = query
    try:
        query = BeautifulSoup(query)
        query = query.text
        summary = summarize.summarize_text(text=query)
    except e:
        summary = summarize.summarize_text(text=query)
    finally:
        return jsonify({ "text": str(summary) })

@app.route('/api/patents')
def get_patents():
    query = request.args["query"]
    wiki = TextBlob(query)
    nouns = wiki.noun_phrases
    query = str(nouns[0])
    request_url = "http://www.patentsview.org/api/patents/query"
    request_body = {
        "q": {
            "_text_any": {
                    "patent_abstract": query
            }
        },
        "o": {
            "per_page": 10
        },
        "f": ["patent_abstract","patent_title","patent_number","inventor_first_name","inventor_last_name","patent_date","forprior_country"]
    }
    network_request = requests.post(url=request_url,json=request_body)
    network_response = network_request.json()
    return jsonify(network_response)

@app.route('/api/companies/search')
def search_companies():
    query = request.args["query"]
    headers = {
        "APPID": config["CONTIFY_APPID"],
        "APPSECRET": config["CONTIFY_APPSECRET"]
    }
    params = { "name": query }
    network_response = requests.get("https://api.contify.com/v2.1/search-company",headers=headers,params=params)
    companies = network_response.json()
    return jsonify(companies)

@app.route('/api/companies/insights')
def fetch_insights():
    company_id = int(request.args["company_id"])
    headers = {
        "APPID": config["CONTIFY_APPID"],
        "APPSECRET": config["CONTIFY_APPSECRET"]
    }
    params = { "company_id": company_id }
    if "location_id" in request.args:
        params = { "company_id": company_id, "location_id": int(request.args["location_id"]) }
    network_response = requests.get("https://api.contify.com/v2.1/insights",headers=headers,params=params)
    network_response = network_response.json()
    return jsonify(network_response)

@app.route('/api/commodities/prices')
def fetch_commodities_prices():
    network_response = requests.get("http://88.99.61.159:4000/getdata?pageSize=100",proxies=proxy_dict)
    network_response = network_response.json()
    return jsonify(network_response)

@app.route('/api/commodities/names')
def fetch_commodities_names():
    if "query" in request.args:
        return jsonify({ "commodities_names": commodities_names[request.args["query"]] })
    else:
        return jsonify({ "commodities_groups": commodities_names })

@app.route('/api/commodities/headlines')
def fetch_commodities_headlines():
    query = request.args["query"]
    network_response = urllib.request.urlopen('http://futures.tradingcharts.com/news/headlines/{}.html?Category={}'.format(query,query)).read()
    html = BeautifulSoup(network_response,features="html.parser")
    articles = []
    for divtag in html.find_all('div',{'class':'news_headlines'}):
        for ultag in divtag.find_all('ul'):
            for litag in ultag.find_all('li'):
                if litag.find('a') is not None:
                    title = litag.find('a').text
                    link = "http://futures.tradingcharts.com"+litag.find('a').get('href')
                    article = { "title": title, "url": link, "description": link }
                    
                    articles.append(article)
                    if len(articles)==10:
                        return jsonify({ "articles": articles, "count": len(articles) })

@app.route('/api/commodities/description')
def extract_description_from_url():
    url = request.args["query"]
    network_response = urllib.request.urlopen(url).read()
    network_response = BeautifulSoup(network_response,features="html.parser")
    hrefs = []
    codes = []

    for divtag in network_response.find_all('div',{'class':'news_story'}):
        description = str(divtag)
        for atag in divtag.find_all('a'):
            href = "http://futures.tradingcharts.com"+atag.get('href')
            if "/chart/" in href:
                chart_split_arr = str(href).split('/')
                code = chart_split_arr[4]
                code = code[0:2]
                hrefs.append(href)
                codes.append(code)
        return jsonify({ 'description':description, 'links': hrefs, 'codes': list(set(codes)) })

@app.route('/api/commodities/futures/codes')
def extract_commodities_futures_codes():
    market_quotes = config["MARKET_QUOTES"]
    if "query" in request.args:
        return jsonify({ "items": market_quotes[request.args["query"]] })
    return jsonify(market_quotes)

@app.route('/api/commodities/futures/forecasts')
def extract_commodities_futures_forecasts():
    code = request.args["query"]
    code = str(code).upper()
    request_url = 'http://ondemand.websol.barchart.com/getQuote.json'
    symbols_string = ''
    for i in range(1,51):
        part = code+'*'+str(i)+','
        symbols_string = symbols_string+part
    data = { 'apikey': websol_api_key, 'fields': websol_fields, 'symbols': symbols_string }
    network_response = requests.post(request_url, data=data)
    network_response = network_response.json()
    return jsonify(network_response)

@app.route('/api/commodities/trades/exports/india')
def extract_exports_india():
    query = request.args["query"]
    with open('exports{}.csv'.format(query)) as exportfile:
        csv_reader = csv.reader(exportfile, delimiter=',')
        line_count = 0
        commodities = list()
        countries = list()
        for row in csv_reader:
            if line_count == 0:
                line_count += 1
            else:
                commodity_name = row[1]
                country_name = row[4]
                amount = row[6]
                line_count += 1
                countries.append(country_name)
                commodities.append({ "commmidity": commodity_name, "country": country_name, "amount": amount })
        
        countries = list(set(countries))
        commodities_main = dict()
        for country in countries:
            country_commodities = list()
            for commodity in commodities:
                if commodity["country"] == country:
                    country_commodities.append(commodity)
            commodities_main[country] = country_commodities
        return jsonify({ "commodities": commodities_main, "countries": countries })

@app.route('/api/commodities/trades/imports/india')
def extract_imports_india():
    year = request.args["query"]
    with open('import{}.json'.format(year)) as importfile:
        output_array = []
        data = json.load(importfile)
        for data_item in data["data"]:
            commodity_name = data_item[1]
            unit_code = data_item[2]
            country_name = data_item[4]
            volume = data_item[3]
            unit_price = data_item[0]

            if not (volume=="N .A" or volume=="NA"):
                output_item = { "commodity": commodity_name, "unit_code": unit_code, "country": country_name, "volume": volume, "unit_price": unit_price }
                output_array.append(output_item)
                
        return jsonify({ "data": output_array })

@app.route('/api/commodities/parameters')
def extract_commodities_parameters():
    query = request.args["query"]
    query = str(query).lower()
    hashtags = []
    config = json.loads(open('config.json').read())
    parameter_hashtags = config["PARAMETERS_HASHTAGS"]
    try:
        hashtags = parameter_hashtags[query]
    except:
        hashtags = []
    finally:
        return jsonify({ "hashtags": parameter_hashtags[query] })        

@app.route('/api/parameters/hashtag/add')
def parameter_to_track():
    display_name = request.args["display"]
    title = request.args["title"]
    category = request.args["category"]
    with open('config.json', 'r+') as f:
        data = json.load(f)
        categories_object = data["PARAMETERS_HASHTAGS"]
        category_items_array = categories_object[category]
        new_item = { "display": display_name, "title": title, "url": "" }
        category_items_array.append(new_item)
        data["PARAMETERS_HASHTAGS"][category] = category_items_array
        f.seek(0)
        json.dump(data, f, indent=4)
        f.truncate()
        config = json.load(f)
        parameter_hashtags = config["PARAMETERS_HASHTAGS"]
        return jsonify({ "items": category_items_array, "category": category })

@app.route('/api/hashtags/query')
def perform_query():
    query = request.args["query"]
    headers = {
        "APPID": config["CONTIFY_APPID"],
        "APPSECRET": config["CONTIFY_APPSECRET"]
    }
    params = { "keyword": query, "sort_by": "relevance" }
    response = requests.get("https://api.contify.com/v2.1/insights?location_id=3",headers=headers,params=params).json()
    return jsonify(response)

@app.route('/api/parameters/extract')
def extract_parameters_from_url():
    query = request.args["query"]
    network_response = urllib.request.urlopen(query).read()
    bs = BeautifulSoup(network_response, features="html.parser")
    core_url = ''
    count = 0
    for divtag in bs.find_all('div',{'class':'fe_content'}):
        for atag in divtag.find_all('a'):
            if count==2:
                break
            href = atag.get('href')
            if "javascript" not in href and "#" not in href and "php" not in href:
                core_url = "http:"+href
                count+=1
    network_response = urllib.request.urlopen(core_url).read()
    bs = BeautifulSoup(network_response, features="html.parser")
    arr = []
    for divtag in bs.find_all('div',{'class':'fe_content'}):
        desc = divtag.text
        wiki = TextBlob(desc)
        for noun in wiki.noun_phrases:
            noun_split = noun.split(" ")
            if len(noun_split) > 1 and len(noun_split) < 3:
                arr.append(noun)
    arr = set(arr)
    return jsonify({ "data": list(arr) })

@app.route('/api/extract/related/article/tenders')
def extract_tags_related_to_article():
    query = request.args["query"]
    tag = "tenders"
    wiki = TextBlob(query)
    x = list()
    search_phrase = tag +" "+ wiki.noun_phrases[0]
    headers = {
        "APPID": config["CONTIFY_APPID"],
        "APPSECRET": config["CONTIFY_APPSECRET"]
    }
    main_dict = dict()
    y = list()
    params = { "keyword": search_phrase, "sort_by": "relevance" }
    response = requests.get("https://api.contify.com/v2.1/insights?location_id=3",headers=headers,params=params).json()
    results = response["results"]
    for article in results:
        item_json = dict()
        item_json["summary"] = article["summary"]
        item_json["title"] = article["title"]
        y.append(item_json)
    return jsonify({ "data": y })

@app.route('/api/extract/related/article/policies')
def extract_tags_related_to_article_two():
    query = request.args["query"]
    tag = "government policy"
    wiki = TextBlob(query)
    x = list()
    for noun in wiki.noun_phrases:
        item = tag + " " + noun
        x.append(noun)
    headers = {
        "APPID": config["CONTIFY_APPID"],
        "APPSECRET": config["CONTIFY_APPSECRET"]
    }
    main_dict = dict()
    y = list()
    for search_phrase in x:
        params = { "keyword": search_phrase, "sort_by": "relevance" }
        response = requests.get("https://api.contify.com/v2.1/insights?location_id=3",headers=headers,params=params).json()
        results = response["results"]
        for article in results:
            item_json = dict()
            item_json["summary"] = article["summary"]
            item_json["title"] = article["title"]
            y.append(item_json)
    return jsonify({ "data": y })


@app.route('/api/query/india')
def query_india():
    query = request.args["query"]
    api_key = config["EVENT_REG_API_KEY"]
    base_url = "http://eventregistry.org/api/v1/article/getArticles?articlesCount=50&apiKey={}&resultType=articles&keyword={}&locationUri=http://en.wikipedia.org/wiki/India&sourceLocationUri=http://en.wikipedia.org/wiki/India&categoryUri=dmoz&articlesSortBy=rel&includeArticleLocation=true".format(api_key,query)
    network_response = requests.get(base_url)
    network_response = network_response.json()
    return jsonify(network_response)

@app.route('/api/related/ibef')
def related_ibef():
    query = request.args["query"]
    query = query.lower()
    with open('verticals.json') as f:
        data = json.load(f)
        data = data["data"]
        for item in data:
            if query in item["name"]:
                return jsonify({ "data": item })
    return jsonify({ "data": None })

@app.route('/api/places/geocode')
def geocode_place():
    query = request.args["query"]
    response = requests.get("https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyCbKE0pHJ1cv2l7wPQT0uwKQ26lZNJb6f4&address="+query).json()
    result = response["results"][0]
    geometry = result["geometry"]
    location = geometry["location"]
    lat = location["lat"]
    lng = location["lng"]
    return jsonify({ "data": { "lat": lat, "lng": lng } })

if __name__ == "__main__":
    app.run(host='0.0.0.0',port=config['PORT'],debug=False,threaded=True)