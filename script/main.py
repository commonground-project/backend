#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import sys
import time
import webbrowser
import requests
from flask import Flask, request
from dotenv import load_dotenv
import threading

# Load environment variables
load_dotenv()
BASE_URL = os.getenv("BASE_URL", "http://localhost:8080")
TOKEN = os.getenv("BEARER_API_TOKEN", "")
REFRESH_TOKEN = os.getenv("REFRESH_TOKEN", "")
LOGIN_PATH = "/api/oauth2/google"

# If there's an endpoint to get current user info, e.g. /api/user/me
USER_PROFILE_ENDPOINT = "/api/user/me"

# Setup endpoint
USER_SETUP_ENDPOINT = "/api/user/setup"

# Refresh token endpoint
REFRESH_ENDPOINT = "/api/jwt/refresh"

app = Flask(__name__)
callback_triggered = False  # A simple flag to indicate if callback has been triggered


@app.route("/callback")
def callback():
    """
    This endpoint will receive the token and refreshToken from the backend after successful OAuth2 login.
    """
    global TOKEN, REFRESH_TOKEN, callback_triggered

    token = request.args.get("token")
    refresh_token = request.args.get("refreshToken")
    redirect_url = request.args.get("r", "")

    if token:
        TOKEN = token
        print(f"[Callback] Received token: {token[:20]}... (truncated)")
    if refresh_token:
        REFRESH_TOKEN = refresh_token
        print(f"[Callback] Received refreshToken: {refresh_token}")

    _update_env("BEARER_API_TOKEN", token)
    _update_env("REFRESH_TOKEN", refresh_token)

    callback_triggered = True

    return f"""
    <h1>Login Success</h1>
    <p>Token (truncated): {token[:20]}...</p>
    <p>RefreshToken: {refresh_token}</p>
    <p>RedirectUrl: {redirect_url}</p>
    <p>You can close this window now and go back to the terminal.</p>
    """.encode("utf-8")


def _update_env(key: str, value: str):
    """
    Update the specified key in .env file with the given value.
    """
    if not value:
        return
    dotenv_path = os.path.join(os.path.dirname(__file__), ".env")
    if not os.path.exists(dotenv_path):
        print("[Warning] .env not found, creating a new one.")
        open(dotenv_path, "w").close()

    with open(dotenv_path, "r", encoding="utf-8") as f:
        lines = f.readlines()

    new_lines = []
    updated = False
    for line in lines:
        if line.strip().startswith(f"{key}="):
            new_lines.append(f'{key}="{value}"\n')
            updated = True
        else:
            new_lines.append(line)

    if not updated:
        new_lines.append(f'{key}="{value}"\n')

    with open(dotenv_path, "w", encoding="utf-8") as f:
        f.writelines(new_lines)

    print(f"[Info] {key} updated in .env: {value[:8]}... (hidden)")


def start_flask_app():
    """
    Start the Flask server in a separate thread.
    """
    app.run(host="127.0.0.1", port=5000, debug=False, use_reloader=False)


def login_flow():
    """
    Launch the local Flask server and open the browser to complete the OAuth2 login flow.
    After obtaining token, check if user role is ROLE_NOT_SETUP and perform setup if needed.
    """
    global callback_triggered
    callback_triggered = False  # Reset the callback flag each time we do a login flow

    # Start Flask in a daemon thread
    server_thread = threading.Thread(target=start_flask_app, daemon=True)
    server_thread.start()

    callback_url = "http://127.0.0.1:5000/callback"
    login_url = f"{BASE_URL}{LOGIN_PATH}?c={callback_url}"
    print(f"[Info] Opening browser for login: {login_url}")

    webbrowser.open(login_url)

    print("[Notice] Waiting for you to finish the login in your browser...")
    # Wait for the callback to set TOKEN
    while not callback_triggered:
        time.sleep(1)

    if TOKEN:
        print("[Info] Successfully obtained the token!")
        # After login, check user role and setup if needed
        check_and_setup_user_if_needed()
    else:
        print("[Error] Token not found. Please check your backend settings.")


def check_and_setup_user_if_needed():
    """
    Check the current user's role.
    If the role is ROLE_NOT_SETUP, then prompt for user setup and refresh tokens.
    """
    user_data = get_user_profile()
    role = user_data.get("role")

    if role == "ROLE_NOT_SETUP":
        print("[Info] Current user role is ROLE_NOT_SETUP. Proceeding with setup.")

        # Read username and nickname from .env or ask user
        username = os.getenv("USERNAME")
        nickname = os.getenv("NICKNAME")

        if not username:
            print("Please input your username:")
            username = input("> ").strip()
            _update_env("USERNAME", username)

        if not nickname:
            print("Please input your nickname:")
            nickname = input("> ").strip()
            _update_env("NICKNAME", nickname)

        # Call user setup endpoint
        user_setup(username, nickname)

        # Refresh the token after setup
        refresh_data = refresh_access_token(REFRESH_TOKEN)
        new_access_token = refresh_data["accessToken"]
        new_refresh_token = refresh_data["refreshToken"]

        # Update .env
        _update_env("BEARER_API_TOKEN", new_access_token)
        _update_env("REFRESH_TOKEN", new_refresh_token)
        print("[Info] Setup completed and tokens refreshed.")
    else:
        print("[Info] User role is not ROLE_NOT_SETUP. No setup required.")


def get_user_profile():
    """
    Retrieve current user profile from the backend, e.g. /api/user/me.
    Must return at least a 'role' field to check if it's ROLE_NOT_SETUP.
    """
    url = f"{BASE_URL}{USER_PROFILE_ENDPOINT}"
    try:
        resp = requests.get(url, headers=get_headers(), timeout=10)
        resp.raise_for_status()
        return resp.json()
    except requests.exceptions.RequestException as e:
        print(f"[Error] Failed to retrieve user profile: {e}")
        sys.exit(1)


def user_setup(username: str, nickname: str):
    """
    Call the setup endpoint to configure user details if role is ROLE_NOT_SETUP.
    """
    url = f"{BASE_URL}{USER_SETUP_ENDPOINT}"
    payload = {
        "username": username,
        "nickname": nickname
    }
    try:
        resp = requests.post(url, json=payload, headers=get_headers(), timeout=10)
        resp.raise_for_status()
        print(f"[Info] User setup successful. Response: {resp.json()}")
    except requests.exceptions.RequestException as e:
        print(f"[Error] Failed to setup user: {e}")
        sys.exit(1)


def refresh_access_token(refresh_token: str):
    """
    Refresh the access token (and get a new refresh token) by calling:
      GET /api/jwt/refresh/{refreshToken}
    Returns a dict with keys: refreshToken, expirationTime, accessToken
    """
    url = f"{BASE_URL}{REFRESH_ENDPOINT}/{refresh_token}"
    try:
        # No "Authorization" header needed for refresh token
        resp = requests.get(url, timeout=10)
        resp.raise_for_status()
        data = resp.json()
        print("[Info] Successfully refreshed token.")
        return data
    except requests.exceptions.RequestException as e:
        print(f"[Error] Failed to refresh token: {e}")
        sys.exit(1)


def get_headers():
    """
    Return request headers with the current token.
    """
    global TOKEN
    if not TOKEN:
        TOKEN = os.getenv("BEARER_API_TOKEN", "")
    return {
        "Authorization": f"Bearer {TOKEN}",
        "Content-Type": "application/json"
    }


def create_issue(title: str, description: str):
    """
    Create an Issue via POST /api/issues
    """
    url = f"{BASE_URL}/api/issues"
    payload = {
        "title": title,
        "description": description
    }
    try:
        resp = requests.post(url, json=payload, headers=get_headers(), timeout=10)
        resp.raise_for_status()
        return resp.json()
    except requests.exceptions.RequestException as e:
        print(f"[Error] Failed to create Issue: {e}")
        sys.exit(1)


def create_viewpoint(issue_id: str, title: str, content: str, facts: list = None):
    """
    Create a Viewpoint via POST /api/issue/{id}/viewpoints
    """
    if facts is None:
        facts = []
    url = f"{BASE_URL}/api/issue/{issue_id}/viewpoints"
    payload = {
        "title": title,
        "content": content,
        "facts": facts
    }
    try:
        resp = requests.post(url, json=payload, headers=get_headers(), timeout=10)
        resp.raise_for_status()
        return resp.json()
    except requests.exceptions.RequestException as e:
        print(f"[Error] Failed to create Viewpoint: {e}")
        sys.exit(1)


def create_fact(title: str, references: list = None):
    """
    Create a Fact via POST /api/facts. You can also pass a list of reference URLs.
    """
    url = f"{BASE_URL}/api/facts"
    payload = {
        "title": title,
        "references": [{"url": ref} for ref in (references or [])]
    }
    try:
        resp = requests.post(url, json=payload, headers=get_headers(), timeout=10)
        resp.raise_for_status()
        return resp.json()
    except requests.exceptions.RequestException as e:
        print(f"[Error] Failed to create Fact: {e}")
        sys.exit(1)


def add_fact_to_issue(issue_id: str, fact_id: str):
    """
    Add a Fact to a specific Issue via POST /api/issue/{id}/facts
    """
    url = f"{BASE_URL}/api/issue/{issue_id}/facts"
    payload = {
        "factIds": [fact_id]
    }
    try:
        resp = requests.post(url, json=payload, headers=get_headers(), timeout=10)
        resp.raise_for_status()
        return resp.json()
    except requests.exceptions.RequestException as e:
        print(f"[Error] Failed to add Fact to Issue: {e}")
        sys.exit(1)


def seed_basic_data():
    """
    Basic: Create 1 Issue and 1 Viewpoint under it.
    """
    issue = create_issue("Basic Issue", "This is a simple Issue for testing.")
    viewpoint = create_viewpoint(
        issue_id=issue["id"],
        title="Basic Viewpoint",
        content="This is a basic viewpoint."
    )
    print(f"[Info] Basic Data Created => Issue(ID={issue['id']}), Viewpoint(ID={viewpoint['id']})")


def seed_medium_data():
    """
    Medium: Create 1 Issue, 2 Viewpoints, and 2 Facts.
            Also link them by specifying 'facts' in the third viewpoint as an example.
    """
    issue = create_issue("Medium Issue", "This is a medium complexity Issue.")
    vp1 = create_viewpoint(issue_id=issue["id"], title="Viewpoint #1", content="Sample viewpoint #1")
    vp2 = create_viewpoint(issue_id=issue["id"], title="Viewpoint #2", content="Sample viewpoint #2")

    fact1 = create_fact("Fact #1 Content", ["https://google.com", "https://www.nycu.edu.tw"])
    fact2 = create_fact("Fact #2 Content", ["https://wikipedia.org", "https://archlinux.cs.nycu.edu.tw"])

    vp3 = create_viewpoint(issue_id=issue["id"],
                           title="Viewpoint #3",
                           content="This is [fact](0) and [fact2](1)",
                           facts=[fact1["id"], fact2["id"]])

    print(f"[Info] Medium Data Created => Issue(ID={issue['id']})")
    print(f" - Viewpoint1(ID={vp1['id']}), Viewpoint2(ID={vp2['id']}), Viewpoint3(ID={vp3['id']})")
    print(f" - Fact1(ID={fact1['id']}), Fact2(ID={fact2['id']})")


def show_current_user_info():
    """
    Call /api/user/me and display user info if token is not empty.
    If token is empty, show an error and do not call backend.
    """
    if not TOKEN:
        print("[Error] No token found. Please run login flow first.")
        return

    try:
        data = get_user_profile()
        print("[Info] Current User Info:")
        # Print everything or just some fields
        print(data)
    except SystemExit:
        # If get_user_profile() calls sys.exit(1) due to an error
        pass
    except Exception as e:
        print(f"[Error] Unexpected error when calling get_user_profile: {e}")


def main():
    """
    Main menu:
      1) Login Flow (obtain token, check role, setup if needed)
      2) Create Basic data (1 Issue + 1 Viewpoint)
      3) Create Medium data (1 Issue + multiple Viewpoints + multiple Facts)
      4) Show Current User Info (/api/user/me)
      q) Quit
    """
    while True:
        print("\n========== Main Menu ==========")
        print("1) Login Flow (obtain token, check role, setup if needed)")
        print("2) Create Basic data (1 Issue + 1 Viewpoint)")
        print("3) Create Medium data (1 Issue + multiple Viewpoints + multiple Facts)")
        print("4) Show Current User Info (/api/user/me)")
        print("q) Quit")
        choice = input("Your choice: ").strip().lower()

        if choice == '1':
            login_flow()
        elif choice == '2':
            seed_basic_data()
        elif choice == '3':
            seed_medium_data()
        elif choice == '4':
            show_current_user_info()
        elif choice == 'q':
            print("Exiting the program.")
            sys.exit(0)
        else:
            print("Invalid selection, please try again.")


if __name__ == "__main__":
    main()
