#!/usr/bin/env python3
"""Build scripts/reference_cities.json with 10 cities per country."""
from __future__ import annotations
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parent
OUTPUT = ROOT / "reference_cities.json"
CITIES_KT = ROOT.parent / "app/src/main/java/com/travelsync/core/data/local/reference/seed/catalog/ReferenceCities.kt"

COUNTRY_ORDER = [
    "US","CA","MX","BR","AR","CL","CO","PE","UY","BO","PY","EC","CR","PA","GT","DO","JM","CU","BS","BB","TT","NI","HN","SV","BZ",
    "GB","FR","DE","IT","ES","PT","NL","BE","CH","AT","IE","SE","NO","DK","FI","PL","CZ","HU","RO","BG","GR","HR","RS","SI","SK","LT","LV","EE","IS","LU","MT","CY","TR","UA","RU","BY","MD","AL","ME","BA","MK","AD","MC","SM","VA",
    "JP","CN","KR","TW","HK","MO","SG","MY","TH","VN","ID","PH","IN","BD","PK","LK","NP","MM","KH","LA","BN","MV","MN","KZ","UZ","GE","AM","AZ","BT","AF",
    "AE","SA","QA","KW","BH","OM","JO","IL","LB","IQ","IR",
    "AU","NZ","FJ","PG","WS","TO","VU",
    "ZA","EG","MA","TN","DZ","KE","TZ","ET","NG","GH","SN","CI","CM","MU","NA","BW","UG","RW","MZ","ZM","AO","ZW",
]

def c(name: str, lat: float, lon: float, alias: str = "") -> dict:
    return {"name": name, "lat": round(lat, 4), "lon": round(lon, 4), "alias": alias}

# Supplemental cities to reach 10 per country (merged with ReferenceCities.kt data)
SUPPLEMENTAL: dict[str, list[dict]] = {}
