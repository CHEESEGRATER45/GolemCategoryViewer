#!/usr/bin/env python3
"""Convert the plugin's categories.yml into the JSON the mod reads.

    python3 yaml_to_json.py categories.yml golem-categories.json

Put the result in your Minecraft folder at config/golem-categories.json (no rebuild needed),
or replace src/main/resources/assets/golemcategories/categories.json and rebuild.
"""
import json, sys

if len(sys.argv) != 3:
    sys.exit("usage: yaml_to_json.py <categories.yml> <out.json>")
cats, cur = {}, None
for line in open(sys.argv[1], encoding="utf-8"):
    t = line.strip()
    if not t or t.startswith("#") or t == "categories:":
        continue
    if t.startswith("- "):
        cats[cur].append(t[2:].strip().strip('"'))
    else:
        cur = t[:-1].strip().strip('"')
        cats[cur] = []
json.dump({"categories": cats}, open(sys.argv[2], "w", encoding="utf-8"), indent=1)
print(f"{len(cats)} categories written to {sys.argv[2]}")
