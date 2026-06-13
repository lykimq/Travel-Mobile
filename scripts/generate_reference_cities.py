#!/usr/bin/env python3
"""Generate ReferenceCities.kt with 10 well-known cities per country."""

from __future__ import annotations

import re
import unicodedata
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
COUNTRIES_FILE = ROOT / "app/src/main/java/com/travelsync/core/data/local/reference/seed/catalog/ReferenceCountries.kt"
OUTPUT_FILE = ROOT / "app/src/main/java/com/travelsync/core/data/local/reference/seed/catalog/ReferenceCities.kt"

# Preserve IDs referenced by demo seed and place catalog.
LEGACY_IDS: dict[tuple[str, str], str] = {
    ("US", "New York"): "city_new_york",
    ("US", "Los Angeles"): "city_los_angeles",
    ("US", "Chicago"): "city_chicago",
    ("US", "Miami"): "city_miami",
    ("US", "San Francisco"): "city_san_francisco",
    ("US", "Las Vegas"): "city_las_vegas",
    ("US", "Washington, D.C."): "city_washington_dc",
    ("US", "Boston"): "city_boston",
    ("US", "Seattle"): "city_seattle",
    ("US", "Honolulu"): "city_honolulu",
    ("CA", "Toronto"): "city_toronto",
    ("CA", "Vancouver"): "city_vancouver",
    ("CA", "Montreal"): "city_montreal",
    ("CA", "Quebec City"): "city_quebec_city",
    ("CA", "Calgary"): "city_calgary",
    ("MX", "Mexico City"): "city_mexico_city",
    ("MX", "Cancun"): "city_cancun",
    ("MX", "Guadalajara"): "city_guadalajara",
    ("BR", "Rio de Janeiro"): "city_rio",
    ("BR", "Sao Paulo"): "city_sao_paulo",
    ("BR", "Brasilia"): "city_brasilia",
    ("BR", "Salvador"): "city_salvador",
    ("BR", "Florianopolis"): "city_florianopolis",
    ("AR", "Buenos Aires"): "city_buenos_aires",
    ("AR", "Mendoza"): "city_mendoza",
    ("AR", "Bariloche"): "city_bariloche",
    ("AR", "Cordoba"): "city_cordoba",
    ("CL", "Santiago"): "city_santiago",
    ("CL", "Valparaiso"): "city_valparaiso",
    ("CO", "Bogota"): "city_bogota",
    ("CO", "Medellin"): "city_medellin",
    ("CO", "Cartagena"): "city_cartagena",
    ("CO", "Santa Marta"): "city_santa_marta",
    ("PE", "Lima"): "city_lima",
    ("PE", "Cusco"): "city_cusco",
    ("PE", "Arequipa"): "city_arequipa",
    ("UY", "Montevideo"): "city_montevideo",
    ("BO", "La Paz"): "city_la_paz",
    ("PY", "Asuncion"): "city_asuncion",
    ("EC", "Quito"): "city_quito",
    ("EC", "Guayaquil"): "city_guayaquil",
    ("CR", "San Jose"): "city_san_jose_cr",
    ("PA", "Panama City"): "city_panama_city",
    ("GT", "Guatemala City"): "city_guatemala_city",
    ("DO", "Santo Domingo"): "city_santo_domingo",
    ("DO", "Punta Cana"): "city_punta_cana",
    ("JM", "Kingston"): "city_kingston",
    ("CU", "Havana"): "city_havana",
    ("CU", "Varadero"): "city_varadero",
    ("BS", "Nassau"): "city_nassau",
    ("BS", "Freeport"): "city_freeport",
    ("NI", "Managua"): "city_managua",
    ("HN", "Tegucigalpa"): "city_tegucigalpa",
    ("SV", "San Salvador"): "city_san_salvador",
    ("BZ", "Belize City"): "city_belize_city",
    ("TT", "Port of Spain"): "city_port_of_spain",
    ("BB", "Bridgetown"): "city_bridgetown",
    ("GB", "London"): "city_london",
    ("GB", "Edinburgh"): "city_edinburgh",
    ("GB", "Manchester"): "city_manchester",
    ("FR", "Paris"): "city_paris",
    ("FR", "Nice"): "city_nice",
    ("FR", "Lyon"): "city_lyon",
    ("DE", "Berlin"): "city_berlin",
    ("DE", "Munich"): "city_munich",
    ("DE", "Frankfurt"): "city_frankfurt",
    ("IT", "Rome"): "city_rome",
    ("IT", "Milan"): "city_milan",
    ("IT", "Venice"): "city_venice",
    ("IT", "Florence"): "city_florence",
    ("ES", "Madrid"): "city_madrid",
    ("ES", "Barcelona"): "city_barcelona",
    ("ES", "Seville"): "city_seville",
    ("PT", "Lisbon"): "city_lisbon",
    ("PT", "Porto"): "city_porto",
    ("NL", "Amsterdam"): "city_amsterdam",
    ("NL", "Rotterdam"): "city_rotterdam",
    ("NL", "The Hague"): "city_the_hague",
    ("BE", "Brussels"): "city_brussels",
    ("BE", "Antwerp"): "city_antwerp",
    ("BE", "Bruges"): "city_bruges",
    ("CH", "Zurich"): "city_zurich",
    ("CH", "Geneva"): "city_geneva",
    ("AT", "Vienna"): "city_vienna",
    ("AT", "Salzburg"): "city_salzburg",
    ("AT", "Innsbruck"): "city_innsbruck",
    ("IE", "Dublin"): "city_dublin",
    ("IE", "Cork"): "city_cork",
    ("IE", "Galway"): "city_galway",
    ("SE", "Stockholm"): "city_stockholm",
    ("SE", "Gothenburg"): "city_gothenburg",
    ("NO", "Oslo"): "city_oslo",
    ("NO", "Bergen"): "city_bergen",
    ("DK", "Copenhagen"): "city_copenhagen",
    ("DK", "Aarhus"): "city_aarhus",
    ("FI", "Helsinki"): "city_helsinki",
    ("FI", "Tampere"): "city_tampere",
    ("PL", "Warsaw"): "city_warsaw",
    ("PL", "Krakow"): "city_krakow",
    ("CZ", "Prague"): "city_prague",
    ("HU", "Budapest"): "city_budapest",
    ("RO", "Bucharest"): "city_bucharest",
    ("BG", "Sofia"): "city_sofia",
    ("GR", "Athens"): "city_athens",
    ("GR", "Santorini"): "city_santorini",
    ("HR", "Dubrovnik"): "city_dubrovnik",
    ("HR", "Zagreb"): "city_zagreb",
    ("RS", "Belgrade"): "city_belgrade",
    ("SI", "Ljubljana"): "city_ljubljana",
    ("SK", "Bratislava"): "city_bratislava",
    ("LT", "Vilnius"): "city_vilnius",
    ("LV", "Riga"): "city_riga",
    ("EE", "Tallinn"): "city_tallinn",
    ("IS", "Reykjavik"): "city_reykjavik",
    ("LU", "Luxembourg City"): "city_luxembourg",
    ("MT", "Valletta"): "city_valletta",
    ("MT", "Sliema"): "city_sliema",
    ("CY", "Nicosia"): "city_nicosia",
    ("CY", "Limassol"): "city_limassol",
    ("CY", "Paphos"): "city_paphos",
    ("TR", "Istanbul"): "city_istanbul",
    ("TR", "Ankara"): "city_ankara",
    ("TR", "Antalya"): "city_antalya",
    ("UA", "Kyiv"): "city_kyiv",
    ("UA", "Lviv"): "city_lviv",
    ("UA", "Odesa"): "city_odessa",
    ("RU", "Moscow"): "city_moscow",
    ("RU", "Saint Petersburg"): "city_st_petersburg",
    ("BY", "Minsk"): "city_minsk",
    ("MD", "Chisinau"): "city_chisinau",
    ("AL", "Tirana"): "city_tirana",
    ("AL", "Durres"): "city_durres",
    ("ME", "Podgorica"): "city_podgorica",
    ("ME", "Kotor"): "city_kotor",
    ("BA", "Sarajevo"): "city_sarajevo",
    ("BA", "Mostar"): "city_mostar",
    ("MK", "Skopje"): "city_skopje",
    ("AD", "Andorra la Vella"): "city_andorra_la_vella",
    ("MC", "Monaco"): "city_monaco",
    ("SM", "San Marino"): "city_san_marino",
    ("VA", "Vatican City"): "city_vatican_city",
    ("JP", "Tokyo"): "city_tokyo",
    ("JP", "Osaka"): "city_osaka",
    ("JP", "Kyoto"): "city_kyoto",
    ("CN", "Beijing"): "city_beijing",
    ("CN", "Shanghai"): "city_shanghai",
    ("CN", "Guangzhou"): "city_guangzhou",
    ("CN", "Shenzhen"): "city_shenzhen",
    ("CN", "Chengdu"): "city_chengdu",
    ("CN", "Xi'an"): "city_xian",
    ("CN", "Hangzhou"): "city_hangzhou",
    ("KR", "Seoul"): "city_seoul",
    ("KR", "Busan"): "city_busan",
    ("TW", "Taipei"): "city_taipei",
    ("TW", "Kaohsiung"): "city_kaohsiung",
    ("TW", "Taichung"): "city_taichung",
    ("HK", "Hong Kong"): "city_hong_kong",
    ("MO", "Macau"): "city_macau",
    ("SG", "Singapore"): "city_singapore",
    ("MY", "Kuala Lumpur"): "city_kuala_lumpur",
    ("MY", "Penang"): "city_penang",
    ("TH", "Bangkok"): "city_bangkok",
    ("TH", "Chiang Mai"): "city_chiang_mai",
    ("TH", "Phuket"): "city_phuket",
    ("VN", "Ho Chi Minh City"): "city_ho_chi_minh",
    ("VN", "Hanoi"): "city_hanoi",
    ("VN", "Da Nang"): "city_da_nang",
    ("VN", "Nha Trang"): "city_nha_trang",
    ("VN", "Hoi An"): "city_hoi_an",
    ("ID", "Jakarta"): "city_jakarta",
    ("ID", "Bali"): "city_bali",
    ("ID", "Surabaya"): "city_surabaya",
    ("ID", "Yogyakarta"): "city_yogyakarta",
    ("PH", "Manila"): "city_manila",
    ("PH", "Cebu"): "city_cebu",
    ("PH", "Boracay"): "city_boracay",
    ("PH", "Palawan"): "city_palawan",
    ("IN", "Mumbai"): "city_mumbai",
    ("IN", "Delhi"): "city_delhi",
    ("IN", "Bengaluru"): "city_bengaluru",
    ("IN", "Jaipur"): "city_jaipur",
    ("IN", "Goa"): "city_goa",
    ("IN", "Kochi"): "city_kerala",
    ("IN", "Chennai"): "city_chennai",
    ("IN", "Kolkata"): "city_kolkata",
    ("BD", "Dhaka"): "city_dhaka",
    ("BD", "Chittagong"): "city_chittagong",
    ("PK", "Karachi"): "city_karachi",
    ("PK", "Lahore"): "city_lahore",
    ("PK", "Islamabad"): "city_islamabad",
    ("LK", "Colombo"): "city_colombo",
    ("LK", "Kandy"): "city_kandy",
    ("LK", "Galle"): "city_galle",
    ("NP", "Kathmandu"): "city_kathmandu",
    ("NP", "Pokhara"): "city_pokhara",
    ("MM", "Yangon"): "city_yangon",
    ("MM", "Mandalay"): "city_mandalay",
    ("MM", "Bagan"): "city_bagan",
    ("KH", "Phnom Penh"): "city_phnom_penh",
    ("KH", "Siem Reap"): "city_siemp_reap",
    ("LA", "Vientiane"): "city_vientiane",
    ("BN", "Bandar Seri Begawan"): "city_bandar_seri_begawan",
    ("MV", "Male"): "city_male",
    ("MN", "Ulaanbaatar"): "city_ulaanbaatar",
    ("KZ", "Almaty"): "city_almaty",
    ("KZ", "Astana"): "city_astana",
    ("UZ", "Tashkent"): "city_tashkent",
    ("UZ", "Samarkand"): "city_samarkand",
    ("UZ", "Bukhara"): "city_bukhara",
    ("GE", "Tbilisi"): "city_tbilisi",
    ("GE", "Batumi"): "city_batumi",
    ("AM", "Yerevan"): "city_yerevan",
    ("AZ", "Baku"): "city_baku",
    ("BT", "Thimphu"): "city_thimphu",
    ("AF", "Kabul"): "city_kabul",
    ("AE", "Dubai"): "city_dubai",
    ("AE", "Abu Dhabi"): "city_abu_dhabi",
    ("SA", "Riyadh"): "city_riyadh",
    ("SA", "Jeddah"): "city_jeddah",
    ("QA", "Doha"): "city_doha",
    ("KW", "Kuwait City"): "city_kuwait_city",
    ("BH", "Manama"): "city_manama",
    ("OM", "Muscat"): "city_muscat",
    ("JO", "Amman"): "city_amman",
    ("IL", "Tel Aviv"): "city_tel_aviv",
    ("IL", "Jerusalem"): "city_jerusalem",
    ("LB", "Beirut"): "city_beirut",
    ("IQ", "Baghdad"): "city_baghdad",
    ("IR", "Tehran"): "city_tehran",
    ("AU", "Sydney"): "city_sydney",
    ("AU", "Melbourne"): "city_melbourne",
    ("AU", "Brisbane"): "city_brisbane",
    ("AU", "Perth"): "city_perth",
    ("NZ", "Auckland"): "city_auckland",
    ("NZ", "Queenstown"): "city_queenstown",
    ("NZ", "Wellington"): "city_wellington",
    ("FJ", "Nadi"): "city_nadi",
    ("PG", "Port Moresby"): "city_port_moresby",
    ("WS", "Apia"): "city_apia",
    ("TO", "Nuku'alofa"): "city_nukualofa",
    ("VU", "Port Vila"): "city_port_vila",
    ("ZA", "Cape Town"): "city_cape_town",
    ("ZA", "Johannesburg"): "city_johannesburg",
    ("EG", "Cairo"): "city_cairo",
    ("EG", "Alexandria"): "city_alexandria",
    ("EG", "Luxor"): "city_luxor",
    ("EG", "Sharm El Sheikh"): "city_sharm_el_sheikh",
    ("MA", "Marrakech"): "city_marrakech",
    ("MA", "Casablanca"): "city_casablanca",
    ("MA", "Fes"): "city_fes",
    ("TN", "Tunis"): "city_tunis",
    ("TN", "Sousse"): "city_sousse",
    ("DZ", "Algiers"): "city_algiers",
    ("DZ", "Oran"): "city_oran",
    ("KE", "Nairobi"): "city_nairobi",
    ("KE", "Mombasa"): "city_mombasa",
    ("TZ", "Zanzibar"): "city_zanzibar",
    ("TZ", "Dar es Salaam"): "city_dar_es_salaam",
    ("TZ", "Arusha"): "city_arusha",
    ("ET", "Addis Ababa"): "city_addis_ababa",
    ("ET", "Lalibela"): "city_lalibela",
    ("NG", "Lagos"): "city_lagos",
    ("NG", "Abuja"): "city_abuja",
    ("GH", "Accra"): "city_accra",
    ("SN", "Dakar"): "city_dakar",
    ("CI", "Abidjan"): "city_abidjan",
    ("CM", "Yaounde"): "city_yaounde",
    ("CM", "Douala"): "city_douala",
    ("MU", "Port Louis"): "city_port_louis",
    ("NA", "Windhoek"): "city_windhoek",
    ("BW", "Gaborone"): "city_gaborone",
    ("UG", "Kampala"): "city_kampala",
    ("RW", "Kigali"): "city_kigali",
    ("MZ", "Maputo"): "city_maputo",
    ("ZM", "Lusaka"): "city_lusaka",
    ("AO", "Luanda"): "city_luanda",
    ("ZW", "Harare"): "city_harare",
    ("ZW", "Victoria Falls"): "city_victoria_falls",
}


def slugify(name: str) -> str:
    normalized = unicodedata.normalize("NFKD", name)
    ascii_name = normalized.encode("ascii", "ignore").decode("ascii")
    slug = re.sub(r"[^a-zA-Z0-9]+", "_", ascii_name.lower()).strip("_")
    slug = re.sub(r"_+", "_", slug)
    return slug or "city"


def city_id(country_code: str, name: str) -> str:
    legacy = LEGACY_IDS.get((country_code, name))
    if legacy:
        return legacy
    return f"city_{slugify(name)}"


def parse_countries() -> list[tuple[str, str, str]]:
    text = COUNTRIES_FILE.read_text(encoding="utf-8")
    pattern = re.compile(
        r'country\("([A-Z]{2})", "[^"]+", RegionCode\.([A-Z_]+), "([A-Z]{3})", \d+\)'
    )
    entries: list[tuple[str, str, str]] = []
    for code, region, currency in pattern.findall(text):
        entries.append((code, region, currency))
    return entries


def fmt_coord(value: float) -> str:
    return f"{value:.4f}"


def emit_city_line(
    city_id_value: str,
    name: str,
    country_code: str,
    currency: str,
    lat: float,
    lon: float,
    alias: str,
    sort_order: int,
) -> str:
    alias_part = f', "{alias}"' if alias else ', ""'
    return (
        f'        city("{city_id_value}", "{name}", "{country_code}", "{currency}", '
        f"{fmt_coord(lat)}, {fmt_coord(lon)}{alias_part}, {sort_order}),"
    )


def load_catalog() -> dict[str, list[tuple[str, float, float, str]]]:
    import json

    data_file = Path(__file__).resolve().parent / "reference_cities.json"
    raw = json.loads(data_file.read_text(encoding="utf-8"))
    catalog: dict[str, list[tuple[str, float, float, str]]] = {}
    for country_code, cities in raw.items():
        catalog[country_code] = [
            (entry["name"], entry["lat"], entry["lon"], entry.get("alias", ""))
            for entry in cities
        ]
    return catalog


def main() -> None:
    countries = parse_countries()
    catalog = load_catalog()
    region_labels = {
        "AMERICAS": "Americas",
        "EUROPE": "Europe",
        "ASIA": "Asia",
        "MIDDLE_EAST": "Middle East",
        "OCEANIA": "Oceania",
        "AFRICA": "Africa",
    }

    missing: list[str] = []
    lines: list[str] = [
        "package com.travelsync.core.data.local.reference.seed.catalog",
        "",
        "import com.travelsync.core.data.local.reference.entity.CityEntity",
        "",
        "internal object ReferenceCities {",
        "",
        "    fun all(): List<CityEntity> = listOf(",
    ]

    sort_order = 1
    current_region: str | None = None
    seen_ids: set[str] = set()

    for country_code, region, currency in countries:
        if region != current_region:
            if current_region is not None:
                lines.append("")
            lines.append(f"        // {region_labels.get(region, region)}")
            current_region = region

        cities = catalog.get(country_code)
        if not cities:
            missing.append(country_code)
            continue
        if len(cities) != 10:
            raise ValueError(f"{country_code} has {len(cities)} cities, expected 10")

        for name, lat, lon, alias in cities:
            cid = city_id(country_code, name)
            if cid in seen_ids:
                raise ValueError(f"Duplicate city id: {cid} ({country_code}, {name})")
            seen_ids.add(cid)
            lines.append(
                emit_city_line(cid, name, country_code, currency, lat, lon, alias, sort_order)
            )
            sort_order += 1

    lines.extend(
        [
            "    )",
            "}",
            "",
        ]
    )

    if missing:
        raise SystemExit(f"Missing city data for: {', '.join(missing)}")

    OUTPUT_FILE.write_text("\n".join(lines), encoding="utf-8")
    total = sort_order - 1
    print(f"Wrote {total} cities for {len(countries)} countries to {OUTPUT_FILE}")


if __name__ == "__main__":
    main()
