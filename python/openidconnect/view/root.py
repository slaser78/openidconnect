import os
from flask import render_template, send_from_directory, session
from . import app


@app.route('/favicon.ico')
def favicon():
    return send_from_directory(os.path.join(app.root_path, '../web/static'),
                               './img/favicon.ico', mimetype='image/vnd.microsoft.icon')


@app.route('/')
def front_page():
    map = {
    }
    return render_template("front.html", **map)


@app.route('/login')
def do_login():
    map = {
    }
    return render_template('login.html', **map)

