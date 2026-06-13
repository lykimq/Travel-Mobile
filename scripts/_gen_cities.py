#!/usr/bin/env python3
import json
from pathlib import Path

OUT = Path(__file__).resolve().parent / "reference_cities.json"

def c(name, lat, lon, alias=""):
    return {"name": name, "lat": round(float(lat), 4), "lon": round(float(lon), 4), "alias": alias}

CATALOG = {}
