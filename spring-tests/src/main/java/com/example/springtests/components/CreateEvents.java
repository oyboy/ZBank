package com.example.springtests.components;

public class CreateEvents {
    public static String createTestEventJson() {
        return """
                {
                    "id": 123456789,
                    "status": "active",
                    "message_type": "market_event",
                    "markets": [{
                        "specifiers": [
                            {"name": "total_base", "value": 5.5},
                            {"name": "match_phase", "value": 1.0}
                        ],
                        "selections": [{
                            "status": 1,
                            "odds": {"price": 2.4, "probability": 1.55555},
                            "selection_type_id": 1
                        }, {
                            "status": 1,
                            "odds": {"price": 4.7, "probability": 2.8888},
                            "selection_type_id": 2
                        }],
                        "market_type_id": 1
                    }]
                }
                """;
    }
    public static String createTestReportJson(){
        return """
                {
                  "id": 67890,
                  "message_type": "market_report",
                  "markets": [
                    {
                      "market_type_id": 2,
                      "selections": [
                        {
                          "selection_type_id": 201,
                          "status": "active"
                        },
                        {
                          "selection_type_id": 202,
                          "status": "suspended"
                        }
                      ]
                    }
                  ]
                }
                """;
    }
    public static String createInvalidJson(){
        return "{ malformed: json, }";
    }
}
