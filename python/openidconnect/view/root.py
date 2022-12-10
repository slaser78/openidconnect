import os
from flask import render_template, send_from_directory, session, redirect, url_for
from flask_login import login_required
from openidconnect import app
from flask_login import logout_user



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
def login():
    map = {
    }
    return render_template('login.html', **map)


@app.route('/logout')
def logout():
    logout_user()
    return redirect(url_for('front_page'))


@app.route('/profile')
@login_required
def profile():
    map = {
    }
    return render_template('profile.html', **map)

