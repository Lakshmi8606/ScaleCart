/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
$(document).ready(function() {

    $(".click-title").mouseenter( function(    e){
        e.preventDefault();
        this.style.cursor="pointer";
    });
    $(".click-title").mousedown( function(event){
        event.preventDefault();
    });

    // Ugly code while this script is shared among several pages
    try{
        refreshHitsPerSecond(true);
    } catch(e){}
    try{
        refreshResponseTimeOverTime(true);
    } catch(e){}
    try{
        refreshResponseTimePercentiles();
    } catch(e){}
});


var responseTimePercentilesInfos = {
        data: {"result": {"minY": 12.0, "minX": 0.0, "maxY": 5903.0, "series": [{"data": [[0.0, 12.0], [0.1, 14.0], [0.2, 14.0], [0.3, 14.0], [0.4, 17.0], [0.5, 18.0], [0.6, 19.0], [0.7, 20.0], [0.8, 20.0], [0.9, 21.0], [1.0, 21.0], [1.1, 22.0], [1.2, 22.0], [1.3, 23.0], [1.4, 23.0], [1.5, 23.0], [1.6, 24.0], [1.7, 24.0], [1.8, 24.0], [1.9, 24.0], [2.0, 25.0], [2.1, 25.0], [2.2, 25.0], [2.3, 25.0], [2.4, 26.0], [2.5, 26.0], [2.6, 27.0], [2.7, 27.0], [2.8, 27.0], [2.9, 29.0], [3.0, 29.0], [3.1, 29.0], [3.2, 29.0], [3.3, 29.0], [3.4, 29.0], [3.5, 30.0], [3.6, 30.0], [3.7, 30.0], [3.8, 31.0], [3.9, 31.0], [4.0, 32.0], [4.1, 32.0], [4.2, 32.0], [4.3, 33.0], [4.4, 33.0], [4.5, 33.0], [4.6, 34.0], [4.7, 34.0], [4.8, 35.0], [4.9, 36.0], [5.0, 36.0], [5.1, 36.0], [5.2, 37.0], [5.3, 37.0], [5.4, 38.0], [5.5, 38.0], [5.6, 38.0], [5.7, 39.0], [5.8, 39.0], [5.9, 39.0], [6.0, 39.0], [6.1, 40.0], [6.2, 40.0], [6.3, 40.0], [6.4, 41.0], [6.5, 42.0], [6.6, 42.0], [6.7, 42.0], [6.8, 42.0], [6.9, 43.0], [7.0, 43.0], [7.1, 43.0], [7.2, 43.0], [7.3, 44.0], [7.4, 44.0], [7.5, 44.0], [7.6, 45.0], [7.7, 45.0], [7.8, 45.0], [7.9, 45.0], [8.0, 45.0], [8.1, 45.0], [8.2, 45.0], [8.3, 45.0], [8.4, 46.0], [8.5, 46.0], [8.6, 46.0], [8.7, 46.0], [8.8, 46.0], [8.9, 46.0], [9.0, 47.0], [9.1, 47.0], [9.2, 47.0], [9.3, 47.0], [9.4, 48.0], [9.5, 48.0], [9.6, 48.0], [9.7, 48.0], [9.8, 48.0], [9.9, 48.0], [10.0, 48.0], [10.1, 48.0], [10.2, 48.0], [10.3, 48.0], [10.4, 49.0], [10.5, 49.0], [10.6, 49.0], [10.7, 49.0], [10.8, 49.0], [10.9, 49.0], [11.0, 50.0], [11.1, 50.0], [11.2, 50.0], [11.3, 50.0], [11.4, 50.0], [11.5, 50.0], [11.6, 51.0], [11.7, 51.0], [11.8, 51.0], [11.9, 51.0], [12.0, 51.0], [12.1, 52.0], [12.2, 52.0], [12.3, 52.0], [12.4, 53.0], [12.5, 53.0], [12.6, 53.0], [12.7, 53.0], [12.8, 53.0], [12.9, 53.0], [13.0, 53.0], [13.1, 53.0], [13.2, 53.0], [13.3, 54.0], [13.4, 54.0], [13.5, 54.0], [13.6, 54.0], [13.7, 55.0], [13.8, 56.0], [13.9, 56.0], [14.0, 56.0], [14.1, 56.0], [14.2, 56.0], [14.3, 56.0], [14.4, 56.0], [14.5, 56.0], [14.6, 56.0], [14.7, 56.0], [14.8, 57.0], [14.9, 57.0], [15.0, 57.0], [15.1, 57.0], [15.2, 57.0], [15.3, 58.0], [15.4, 58.0], [15.5, 59.0], [15.6, 59.0], [15.7, 60.0], [15.8, 60.0], [15.9, 61.0], [16.0, 61.0], [16.1, 62.0], [16.2, 62.0], [16.3, 63.0], [16.4, 63.0], [16.5, 63.0], [16.6, 63.0], [16.7, 63.0], [16.8, 63.0], [16.9, 63.0], [17.0, 63.0], [17.1, 64.0], [17.2, 64.0], [17.3, 65.0], [17.4, 65.0], [17.5, 66.0], [17.6, 66.0], [17.7, 66.0], [17.8, 66.0], [17.9, 66.0], [18.0, 66.0], [18.1, 67.0], [18.2, 67.0], [18.3, 67.0], [18.4, 67.0], [18.5, 68.0], [18.6, 68.0], [18.7, 68.0], [18.8, 68.0], [18.9, 68.0], [19.0, 69.0], [19.1, 69.0], [19.2, 69.0], [19.3, 70.0], [19.4, 70.0], [19.5, 70.0], [19.6, 70.0], [19.7, 70.0], [19.8, 70.0], [19.9, 70.0], [20.0, 70.0], [20.1, 70.0], [20.2, 71.0], [20.3, 71.0], [20.4, 71.0], [20.5, 71.0], [20.6, 72.0], [20.7, 72.0], [20.8, 72.0], [20.9, 73.0], [21.0, 73.0], [21.1, 73.0], [21.2, 74.0], [21.3, 74.0], [21.4, 74.0], [21.5, 74.0], [21.6, 75.0], [21.7, 75.0], [21.8, 75.0], [21.9, 75.0], [22.0, 75.0], [22.1, 75.0], [22.2, 76.0], [22.3, 76.0], [22.4, 76.0], [22.5, 76.0], [22.6, 76.0], [22.7, 76.0], [22.8, 76.0], [22.9, 77.0], [23.0, 77.0], [23.1, 77.0], [23.2, 77.0], [23.3, 77.0], [23.4, 78.0], [23.5, 78.0], [23.6, 78.0], [23.7, 78.0], [23.8, 78.0], [23.9, 78.0], [24.0, 78.0], [24.1, 78.0], [24.2, 78.0], [24.3, 78.0], [24.4, 79.0], [24.5, 79.0], [24.6, 79.0], [24.7, 79.0], [24.8, 79.0], [24.9, 79.0], [25.0, 79.0], [25.1, 79.0], [25.2, 79.0], [25.3, 79.0], [25.4, 79.0], [25.5, 80.0], [25.6, 80.0], [25.7, 80.0], [25.8, 80.0], [25.9, 80.0], [26.0, 80.0], [26.1, 80.0], [26.2, 80.0], [26.3, 80.0], [26.4, 81.0], [26.5, 81.0], [26.6, 81.0], [26.7, 82.0], [26.8, 82.0], [26.9, 82.0], [27.0, 82.0], [27.1, 83.0], [27.2, 83.0], [27.3, 83.0], [27.4, 83.0], [27.5, 84.0], [27.6, 84.0], [27.7, 84.0], [27.8, 84.0], [27.9, 85.0], [28.0, 85.0], [28.1, 85.0], [28.2, 85.0], [28.3, 85.0], [28.4, 85.0], [28.5, 85.0], [28.6, 86.0], [28.7, 86.0], [28.8, 86.0], [28.9, 86.0], [29.0, 86.0], [29.1, 86.0], [29.2, 87.0], [29.3, 87.0], [29.4, 87.0], [29.5, 87.0], [29.6, 87.0], [29.7, 87.0], [29.8, 87.0], [29.9, 88.0], [30.0, 88.0], [30.1, 88.0], [30.2, 88.0], [30.3, 89.0], [30.4, 89.0], [30.5, 89.0], [30.6, 89.0], [30.7, 89.0], [30.8, 90.0], [30.9, 90.0], [31.0, 90.0], [31.1, 90.0], [31.2, 90.0], [31.3, 91.0], [31.4, 91.0], [31.5, 91.0], [31.6, 91.0], [31.7, 91.0], [31.8, 92.0], [31.9, 92.0], [32.0, 92.0], [32.1, 93.0], [32.2, 93.0], [32.3, 93.0], [32.4, 93.0], [32.5, 94.0], [32.6, 94.0], [32.7, 94.0], [32.8, 94.0], [32.9, 95.0], [33.0, 95.0], [33.1, 96.0], [33.2, 96.0], [33.3, 96.0], [33.4, 96.0], [33.5, 96.0], [33.6, 96.0], [33.7, 97.0], [33.8, 97.0], [33.9, 98.0], [34.0, 98.0], [34.1, 98.0], [34.2, 98.0], [34.3, 100.0], [34.4, 100.0], [34.5, 100.0], [34.6, 100.0], [34.7, 100.0], [34.8, 101.0], [34.9, 101.0], [35.0, 101.0], [35.1, 101.0], [35.2, 101.0], [35.3, 101.0], [35.4, 101.0], [35.5, 102.0], [35.6, 102.0], [35.7, 103.0], [35.8, 103.0], [35.9, 103.0], [36.0, 103.0], [36.1, 104.0], [36.2, 104.0], [36.3, 104.0], [36.4, 104.0], [36.5, 105.0], [36.6, 105.0], [36.7, 105.0], [36.8, 105.0], [36.9, 105.0], [37.0, 106.0], [37.1, 107.0], [37.2, 107.0], [37.3, 107.0], [37.4, 107.0], [37.5, 107.0], [37.6, 107.0], [37.7, 108.0], [37.8, 108.0], [37.9, 108.0], [38.0, 108.0], [38.1, 109.0], [38.2, 109.0], [38.3, 110.0], [38.4, 111.0], [38.5, 111.0], [38.6, 112.0], [38.7, 112.0], [38.8, 112.0], [38.9, 112.0], [39.0, 112.0], [39.1, 113.0], [39.2, 113.0], [39.3, 113.0], [39.4, 113.0], [39.5, 113.0], [39.6, 114.0], [39.7, 114.0], [39.8, 114.0], [39.9, 115.0], [40.0, 115.0], [40.1, 116.0], [40.2, 116.0], [40.3, 116.0], [40.4, 116.0], [40.5, 116.0], [40.6, 116.0], [40.7, 117.0], [40.8, 117.0], [40.9, 117.0], [41.0, 117.0], [41.1, 118.0], [41.2, 118.0], [41.3, 118.0], [41.4, 118.0], [41.5, 118.0], [41.6, 119.0], [41.7, 119.0], [41.8, 119.0], [41.9, 119.0], [42.0, 119.0], [42.1, 119.0], [42.2, 119.0], [42.3, 119.0], [42.4, 120.0], [42.5, 120.0], [42.6, 121.0], [42.7, 121.0], [42.8, 122.0], [42.9, 122.0], [43.0, 122.0], [43.1, 122.0], [43.2, 122.0], [43.3, 122.0], [43.4, 123.0], [43.5, 124.0], [43.6, 124.0], [43.7, 124.0], [43.8, 124.0], [43.9, 124.0], [44.0, 124.0], [44.1, 124.0], [44.2, 124.0], [44.3, 125.0], [44.4, 125.0], [44.5, 126.0], [44.6, 126.0], [44.7, 127.0], [44.8, 127.0], [44.9, 127.0], [45.0, 127.0], [45.1, 128.0], [45.2, 128.0], [45.3, 128.0], [45.4, 128.0], [45.5, 128.0], [45.6, 128.0], [45.7, 129.0], [45.8, 129.0], [45.9, 130.0], [46.0, 130.0], [46.1, 131.0], [46.2, 131.0], [46.3, 131.0], [46.4, 131.0], [46.5, 131.0], [46.6, 131.0], [46.7, 131.0], [46.8, 131.0], [46.9, 132.0], [47.0, 132.0], [47.1, 133.0], [47.2, 133.0], [47.3, 133.0], [47.4, 133.0], [47.5, 133.0], [47.6, 133.0], [47.7, 135.0], [47.8, 135.0], [47.9, 135.0], [48.0, 135.0], [48.1, 136.0], [48.2, 136.0], [48.3, 137.0], [48.4, 137.0], [48.5, 138.0], [48.6, 139.0], [48.7, 139.0], [48.8, 139.0], [48.9, 139.0], [49.0, 141.0], [49.1, 141.0], [49.2, 142.0], [49.3, 142.0], [49.4, 142.0], [49.5, 143.0], [49.6, 143.0], [49.7, 143.0], [49.8, 143.0], [49.9, 143.0], [50.0, 144.0], [50.1, 145.0], [50.2, 145.0], [50.3, 145.0], [50.4, 146.0], [50.5, 146.0], [50.6, 147.0], [50.7, 147.0], [50.8, 147.0], [50.9, 147.0], [51.0, 147.0], [51.1, 149.0], [51.2, 149.0], [51.3, 149.0], [51.4, 150.0], [51.5, 150.0], [51.6, 151.0], [51.7, 151.0], [51.8, 151.0], [51.9, 152.0], [52.0, 153.0], [52.1, 154.0], [52.2, 154.0], [52.3, 155.0], [52.4, 155.0], [52.5, 157.0], [52.6, 158.0], [52.7, 159.0], [52.8, 159.0], [52.9, 160.0], [53.0, 160.0], [53.1, 160.0], [53.2, 160.0], [53.3, 160.0], [53.4, 160.0], [53.5, 162.0], [53.6, 162.0], [53.7, 162.0], [53.8, 162.0], [53.9, 164.0], [54.0, 165.0], [54.1, 165.0], [54.2, 165.0], [54.3, 165.0], [54.4, 165.0], [54.5, 166.0], [54.6, 166.0], [54.7, 167.0], [54.8, 167.0], [54.9, 167.0], [55.0, 167.0], [55.1, 167.0], [55.2, 168.0], [55.3, 168.0], [55.4, 171.0], [55.5, 171.0], [55.6, 171.0], [55.7, 172.0], [55.8, 172.0], [55.9, 172.0], [56.0, 173.0], [56.1, 174.0], [56.2, 175.0], [56.3, 175.0], [56.4, 176.0], [56.5, 176.0], [56.6, 177.0], [56.7, 177.0], [56.8, 177.0], [56.9, 178.0], [57.0, 179.0], [57.1, 179.0], [57.2, 179.0], [57.3, 181.0], [57.4, 181.0], [57.5, 181.0], [57.6, 182.0], [57.7, 182.0], [57.8, 183.0], [57.9, 183.0], [58.0, 184.0], [58.1, 184.0], [58.2, 184.0], [58.3, 185.0], [58.4, 185.0], [58.5, 186.0], [58.6, 186.0], [58.7, 186.0], [58.8, 187.0], [58.9, 187.0], [59.0, 188.0], [59.1, 189.0], [59.2, 190.0], [59.3, 190.0], [59.4, 191.0], [59.5, 191.0], [59.6, 191.0], [59.7, 191.0], [59.8, 192.0], [59.9, 193.0], [60.0, 193.0], [60.1, 194.0], [60.2, 194.0], [60.3, 195.0], [60.4, 195.0], [60.5, 197.0], [60.6, 197.0], [60.7, 199.0], [60.8, 199.0], [60.9, 200.0], [61.0, 200.0], [61.1, 200.0], [61.2, 200.0], [61.3, 201.0], [61.4, 201.0], [61.5, 201.0], [61.6, 202.0], [61.7, 202.0], [61.8, 203.0], [61.9, 203.0], [62.0, 203.0], [62.1, 204.0], [62.2, 205.0], [62.3, 205.0], [62.4, 205.0], [62.5, 206.0], [62.6, 206.0], [62.7, 206.0], [62.8, 207.0], [62.9, 207.0], [63.0, 207.0], [63.1, 208.0], [63.2, 210.0], [63.3, 212.0], [63.4, 213.0], [63.5, 213.0], [63.6, 213.0], [63.7, 215.0], [63.8, 215.0], [63.9, 215.0], [64.0, 216.0], [64.1, 216.0], [64.2, 216.0], [64.3, 217.0], [64.4, 217.0], [64.5, 218.0], [64.6, 218.0], [64.7, 218.0], [64.8, 219.0], [64.9, 219.0], [65.0, 220.0], [65.1, 220.0], [65.2, 221.0], [65.3, 221.0], [65.4, 222.0], [65.5, 222.0], [65.6, 222.0], [65.7, 222.0], [65.8, 222.0], [65.9, 223.0], [66.0, 223.0], [66.1, 224.0], [66.2, 225.0], [66.3, 225.0], [66.4, 225.0], [66.5, 225.0], [66.6, 226.0], [66.7, 226.0], [66.8, 226.0], [66.9, 226.0], [67.0, 227.0], [67.1, 227.0], [67.2, 227.0], [67.3, 227.0], [67.4, 228.0], [67.5, 229.0], [67.6, 230.0], [67.7, 231.0], [67.8, 231.0], [67.9, 232.0], [68.0, 234.0], [68.1, 234.0], [68.2, 234.0], [68.3, 234.0], [68.4, 234.0], [68.5, 235.0], [68.6, 235.0], [68.7, 235.0], [68.8, 235.0], [68.9, 235.0], [69.0, 235.0], [69.1, 235.0], [69.2, 235.0], [69.3, 236.0], [69.4, 236.0], [69.5, 236.0], [69.6, 237.0], [69.7, 237.0], [69.8, 238.0], [69.9, 238.0], [70.0, 238.0], [70.1, 238.0], [70.2, 239.0], [70.3, 239.0], [70.4, 239.0], [70.5, 239.0], [70.6, 240.0], [70.7, 240.0], [70.8, 242.0], [70.9, 242.0], [71.0, 242.0], [71.1, 242.0], [71.2, 242.0], [71.3, 243.0], [71.4, 243.0], [71.5, 243.0], [71.6, 243.0], [71.7, 243.0], [71.8, 244.0], [71.9, 244.0], [72.0, 245.0], [72.1, 245.0], [72.2, 245.0], [72.3, 245.0], [72.4, 245.0], [72.5, 245.0], [72.6, 246.0], [72.7, 246.0], [72.8, 247.0], [72.9, 247.0], [73.0, 247.0], [73.1, 248.0], [73.2, 249.0], [73.3, 250.0], [73.4, 250.0], [73.5, 251.0], [73.6, 252.0], [73.7, 252.0], [73.8, 252.0], [73.9, 253.0], [74.0, 253.0], [74.1, 253.0], [74.2, 254.0], [74.3, 254.0], [74.4, 255.0], [74.5, 256.0], [74.6, 256.0], [74.7, 257.0], [74.8, 258.0], [74.9, 259.0], [75.0, 260.0], [75.1, 261.0], [75.2, 261.0], [75.3, 261.0], [75.4, 262.0], [75.5, 263.0], [75.6, 264.0], [75.7, 266.0], [75.8, 266.0], [75.9, 267.0], [76.0, 267.0], [76.1, 267.0], [76.2, 267.0], [76.3, 267.0], [76.4, 269.0], [76.5, 273.0], [76.6, 275.0], [76.7, 276.0], [76.8, 277.0], [76.9, 279.0], [77.0, 279.0], [77.1, 280.0], [77.2, 280.0], [77.3, 281.0], [77.4, 281.0], [77.5, 281.0], [77.6, 282.0], [77.7, 282.0], [77.8, 283.0], [77.9, 284.0], [78.0, 284.0], [78.1, 285.0], [78.2, 285.0], [78.3, 287.0], [78.4, 287.0], [78.5, 287.0], [78.6, 287.0], [78.7, 290.0], [78.8, 290.0], [78.9, 293.0], [79.0, 293.0], [79.1, 293.0], [79.2, 295.0], [79.3, 295.0], [79.4, 296.0], [79.5, 296.0], [79.6, 296.0], [79.7, 297.0], [79.8, 300.0], [79.9, 300.0], [80.0, 301.0], [80.1, 301.0], [80.2, 302.0], [80.3, 302.0], [80.4, 302.0], [80.5, 305.0], [80.6, 306.0], [80.7, 306.0], [80.8, 308.0], [80.9, 309.0], [81.0, 309.0], [81.1, 311.0], [81.2, 311.0], [81.3, 315.0], [81.4, 315.0], [81.5, 315.0], [81.6, 315.0], [81.7, 316.0], [81.8, 319.0], [81.9, 319.0], [82.0, 320.0], [82.1, 321.0], [82.2, 321.0], [82.3, 322.0], [82.4, 322.0], [82.5, 324.0], [82.6, 326.0], [82.7, 326.0], [82.8, 328.0], [82.9, 328.0], [83.0, 329.0], [83.1, 330.0], [83.2, 330.0], [83.3, 332.0], [83.4, 335.0], [83.5, 335.0], [83.6, 335.0], [83.7, 336.0], [83.8, 337.0], [83.9, 337.0], [84.0, 339.0], [84.1, 340.0], [84.2, 340.0], [84.3, 341.0], [84.4, 342.0], [84.5, 343.0], [84.6, 344.0], [84.7, 346.0], [84.8, 347.0], [84.9, 348.0], [85.0, 348.0], [85.1, 349.0], [85.2, 350.0], [85.3, 351.0], [85.4, 351.0], [85.5, 352.0], [85.6, 352.0], [85.7, 355.0], [85.8, 356.0], [85.9, 356.0], [86.0, 357.0], [86.1, 359.0], [86.2, 359.0], [86.3, 362.0], [86.4, 363.0], [86.5, 363.0], [86.6, 364.0], [86.7, 365.0], [86.8, 365.0], [86.9, 367.0], [87.0, 367.0], [87.1, 367.0], [87.2, 368.0], [87.3, 372.0], [87.4, 381.0], [87.5, 386.0], [87.6, 388.0], [87.7, 389.0], [87.8, 389.0], [87.9, 392.0], [88.0, 394.0], [88.1, 397.0], [88.2, 399.0], [88.3, 400.0], [88.4, 402.0], [88.5, 402.0], [88.6, 402.0], [88.7, 403.0], [88.8, 404.0], [88.9, 405.0], [89.0, 407.0], [89.1, 408.0], [89.2, 408.0], [89.3, 417.0], [89.4, 419.0], [89.5, 419.0], [89.6, 419.0], [89.7, 422.0], [89.8, 422.0], [89.9, 424.0], [90.0, 425.0], [90.1, 425.0], [90.2, 426.0], [90.3, 427.0], [90.4, 427.0], [90.5, 429.0], [90.6, 430.0], [90.7, 433.0], [90.8, 433.0], [90.9, 434.0], [91.0, 437.0], [91.1, 439.0], [91.2, 441.0], [91.3, 442.0], [91.4, 446.0], [91.5, 450.0], [91.6, 450.0], [91.7, 455.0], [91.8, 456.0], [91.9, 462.0], [92.0, 462.0], [92.1, 465.0], [92.2, 468.0], [92.3, 470.0], [92.4, 472.0], [92.5, 472.0], [92.6, 473.0], [92.7, 474.0], [92.8, 484.0], [92.9, 487.0], [93.0, 488.0], [93.1, 489.0], [93.2, 491.0], [93.3, 500.0], [93.4, 501.0], [93.5, 503.0], [93.6, 515.0], [93.7, 520.0], [93.8, 521.0], [93.9, 526.0], [94.0, 532.0], [94.1, 542.0], [94.2, 553.0], [94.3, 553.0], [94.4, 554.0], [94.5, 555.0], [94.6, 563.0], [94.7, 587.0], [94.8, 605.0], [94.9, 608.0], [95.0, 1073.0], [95.1, 1320.0], [95.2, 1371.0], [95.3, 1417.0], [95.4, 1615.0], [95.5, 1643.0], [95.6, 1647.0], [95.7, 1912.0], [95.8, 1983.0], [95.9, 2023.0], [96.0, 2053.0], [96.1, 2142.0], [96.2, 2367.0], [96.3, 2396.0], [96.4, 2488.0], [96.5, 2556.0], [96.6, 2643.0], [96.7, 2742.0], [96.8, 2982.0], [96.9, 3055.0], [97.0, 3055.0], [97.1, 3178.0], [97.2, 3390.0], [97.3, 3451.0], [97.4, 3476.0], [97.5, 3546.0], [97.6, 3691.0], [97.7, 3742.0], [97.8, 3840.0], [97.9, 4078.0], [98.0, 4191.0], [98.1, 4201.0], [98.2, 4383.0], [98.3, 4523.0], [98.4, 4587.0], [98.5, 4616.0], [98.6, 4641.0], [98.7, 4781.0], [98.8, 5001.0], [98.9, 5027.0], [99.0, 5157.0], [99.1, 5279.0], [99.2, 5299.0], [99.3, 5476.0], [99.4, 5539.0], [99.5, 5573.0], [99.6, 5636.0], [99.7, 5816.0], [99.8, 5848.0], [99.9, 5903.0]], "isOverall": false, "label": "HTTP Request", "isController": false}], "supportsControllersDiscrimination": true, "maxX": 100.0, "title": "Response Time Percentiles"}},
        getOptions: function() {
            return {
                series: {
                    points: { show: false }
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimePercentiles'
                },
                xaxis: {
                    tickDecimals: 1,
                    axisLabel: "Percentiles",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Percentile value in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : %x.2 percentile was %y ms"
                },
                selection: { mode: "xy" },
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesResponseTimePercentiles"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimesPercentiles"), dataset, options);
            // setup overview
            $.plot($("#overviewResponseTimesPercentiles"), dataset, prepareOverviewOptions(options));
        }
};

/**
 * @param elementId Id of element where we display message
 */
function setEmptyGraph(elementId) {
    $(function() {
        $(elementId).text("No graph series with filter="+seriesFilter);
    });
}

// Response times percentiles
function refreshResponseTimePercentiles() {
    var infos = responseTimePercentilesInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyResponseTimePercentiles");
        return;
    }
    if (isGraph($("#flotResponseTimesPercentiles"))){
        infos.createGraph();
    } else {
        var choiceContainer = $("#choicesResponseTimePercentiles");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimesPercentiles", "#overviewResponseTimesPercentiles");
        $('#bodyResponseTimePercentiles .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
}

var responseTimeDistributionInfos = {
        data: {"result": {"minY": 1.0, "minX": 0.0, "maxY": 343.0, "series": [{"data": [[0.0, 343.0], [600.0, 2.0], [1000.0, 1.0], [1300.0, 2.0], [1400.0, 1.0], [1600.0, 3.0], [100.0, 265.0], [1900.0, 2.0], [2000.0, 2.0], [2100.0, 1.0], [2300.0, 2.0], [2400.0, 1.0], [2500.0, 1.0], [2600.0, 1.0], [2700.0, 1.0], [2900.0, 1.0], [3000.0, 2.0], [3100.0, 1.0], [3300.0, 1.0], [200.0, 190.0], [3400.0, 2.0], [3500.0, 1.0], [3700.0, 1.0], [3600.0, 1.0], [3800.0, 1.0], [4000.0, 1.0], [4200.0, 1.0], [4300.0, 1.0], [4100.0, 1.0], [4600.0, 2.0], [4500.0, 2.0], [4700.0, 1.0], [300.0, 85.0], [5000.0, 2.0], [5100.0, 1.0], [5200.0, 2.0], [5600.0, 1.0], [5500.0, 2.0], [5400.0, 1.0], [5800.0, 2.0], [5900.0, 1.0], [400.0, 50.0], [500.0, 15.0]], "isOverall": false, "label": "HTTP Request", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 100, "maxX": 5900.0, "title": "Response Time Distribution"}},
        getOptions: function() {
            var granularity = this.data.result.granularity;
            return {
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimeDistribution'
                },
                xaxis:{
                    axisLabel: "Response times in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of responses",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                bars : {
                    show: true,
                    barWidth: this.data.result.granularity
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: function(label, xval, yval, flotItem){
                        return yval + " responses for " + label + " were between " + xval + " and " + (xval + granularity) + " ms";
                    }
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimeDistribution"), prepareData(data.result.series, $("#choicesResponseTimeDistribution")), options);
        }

};

// Response time distribution
function refreshResponseTimeDistribution() {
    var infos = responseTimeDistributionInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyResponseTimeDistribution");
        return;
    }
    if (isGraph($("#flotResponseTimeDistribution"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesResponseTimeDistribution");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        $('#footerResponseTimeDistribution .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};


var syntheticResponseTimeDistributionInfos = {
        data: {"result": {"minY": 20.0, "minX": 0.0, "ticks": [[0, "Requests having \nresponse time <= 500ms"], [1, "Requests having \nresponse time > 500ms and <= 1,500ms"], [2, "Requests having \nresponse time > 1,500ms"], [3, "Requests in error"]], "maxY": 934.0, "series": [{"data": [[0.0, 934.0]], "color": "#9ACD32", "isOverall": false, "label": "Requests having \nresponse time <= 500ms", "isController": false}, {"data": [[1.0, 20.0]], "color": "yellow", "isOverall": false, "label": "Requests having \nresponse time > 500ms and <= 1,500ms", "isController": false}, {"data": [[2.0, 46.0]], "color": "orange", "isOverall": false, "label": "Requests having \nresponse time > 1,500ms", "isController": false}, {"data": [], "color": "#FF6347", "isOverall": false, "label": "Requests in error", "isController": false}], "supportsControllersDiscrimination": false, "maxX": 2.0, "title": "Synthetic Response Times Distribution"}},
        getOptions: function() {
            return {
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendSyntheticResponseTimeDistribution'
                },
                xaxis:{
                    axisLabel: "Response times ranges",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                    tickLength:0,
                    min:-0.5,
                    max:3.5
                },
                yaxis: {
                    axisLabel: "Number of responses",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                bars : {
                    show: true,
                    align: "center",
                    barWidth: 0.25,
                    fill:.75
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: function(label, xval, yval, flotItem){
                        return yval + " " + label;
                    }
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var options = this.getOptions();
            prepareOptions(options, data);
            options.xaxis.ticks = data.result.ticks;
            $.plot($("#flotSyntheticResponseTimeDistribution"), prepareData(data.result.series, $("#choicesSyntheticResponseTimeDistribution")), options);
        }

};

// Response time distribution
function refreshSyntheticResponseTimeDistribution() {
    var infos = syntheticResponseTimeDistributionInfos;
    prepareSeries(infos.data, true);
    if (isGraph($("#flotSyntheticResponseTimeDistribution"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesSyntheticResponseTimeDistribution");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        $('#footerSyntheticResponseTimeDistribution .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var activeThreadsOverTimeInfos = {
        data: {"result": {"minY": 45.880999999999965, "minX": 1.78860642E12, "maxY": 45.880999999999965, "series": [{"data": [[1.78860642E12, 45.880999999999965]], "isOverall": false, "label": "Thread Group", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.78860642E12, "title": "Active Threads Over Time"}},
        getOptions: function() {
            return {
                series: {
                    stack: true,
                    lines: {
                        show: true,
                        fill: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of active threads",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 6,
                    show: true,
                    container: '#legendActiveThreadsOverTime'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                selection: {
                    mode: 'xy'
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : At %x there were %y active threads"
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesActiveThreadsOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotActiveThreadsOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewActiveThreadsOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Active Threads Over Time
function refreshActiveThreadsOverTime(fixTimestamps) {
    var infos = activeThreadsOverTimeInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 19800000);
    }
    if(isGraph($("#flotActiveThreadsOverTime"))) {
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesActiveThreadsOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotActiveThreadsOverTime", "#overviewActiveThreadsOverTime");
        $('#footerActiveThreadsOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var timeVsThreadsInfos = {
        data: {"result": {"minY": 18.0, "minX": 1.0, "maxY": 426.98378378378374, "series": [{"data": [[33.0, 133.61904761904765], [32.0, 70.14285714285714], [2.0, 18.0], [35.0, 112.0], [34.0, 243.0], [37.0, 95.0], [36.0, 73.0], [39.0, 70.90909090909092], [38.0, 134.6153846153846], [41.0, 106.0], [40.0, 172.66666666666666], [43.0, 131.0], [42.0, 106.25], [45.0, 102.0], [44.0, 63.0], [47.0, 89.43243243243244], [46.0, 115.625], [49.0, 145.6], [48.0, 98.0], [50.0, 426.98378378378374], [4.0, 29.0], [5.0, 18.0], [6.0, 56.0], [8.0, 34.2], [9.0, 56.0], [11.0, 108.0], [12.0, 36.666666666666664], [13.0, 92.66666666666667], [14.0, 71.33333333333333], [16.0, 81.5], [1.0, 18.5], [17.0, 56.0], [18.0, 63.0], [19.0, 66.0], [21.0, 106.33333333333333], [22.0, 83.0], [23.0, 88.0], [24.0, 91.0], [25.0, 166.5], [26.0, 144.99999999999997], [27.0, 92.4], [28.0, 122.99999999999999], [29.0, 50.333333333333336], [30.0, 136.0]], "isOverall": false, "label": "HTTP Request", "isController": false}, {"data": [[45.880999999999965, 343.02400000000017]], "isOverall": false, "label": "HTTP Request-Aggregated", "isController": false}], "supportsControllersDiscrimination": true, "maxX": 50.0, "title": "Time VS Threads"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    axisLabel: "Number of active threads",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average response times in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: { noColumns: 2,show: true, container: '#legendTimeVsThreads' },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s: At %x.2 active threads, Average response time was %y.2 ms"
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesTimeVsThreads"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotTimesVsThreads"), dataset, options);
            // setup overview
            $.plot($("#overviewTimesVsThreads"), dataset, prepareOverviewOptions(options));
        }
};

// Time vs threads
function refreshTimeVsThreads(){
    var infos = timeVsThreadsInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyTimeVsThreads");
        return;
    }
    if(isGraph($("#flotTimesVsThreads"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesTimeVsThreads");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotTimesVsThreads", "#overviewTimesVsThreads");
        $('#footerTimeVsThreads .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var bytesThroughputOverTimeInfos = {
        data : {"result": {"minY": 2166.6666666666665, "minX": 1.78860642E12, "maxY": 11500.0, "series": [{"data": [[1.78860642E12, 11500.0]], "isOverall": false, "label": "Bytes received per second", "isController": false}, {"data": [[1.78860642E12, 2166.6666666666665]], "isOverall": false, "label": "Bytes sent per second", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.78860642E12, "title": "Bytes Throughput Over Time"}},
        getOptions : function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity) ,
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Bytes / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendBytesThroughputOverTime'
                },
                selection: {
                    mode: "xy"
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y"
                }
            };
        },
        createGraph : function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesBytesThroughputOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotBytesThroughputOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewBytesThroughputOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Bytes throughput Over Time
function refreshBytesThroughputOverTime(fixTimestamps) {
    var infos = bytesThroughputOverTimeInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 19800000);
    }
    if(isGraph($("#flotBytesThroughputOverTime"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesBytesThroughputOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotBytesThroughputOverTime", "#overviewBytesThroughputOverTime");
        $('#footerBytesThroughputOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
}

var responseTimesOverTimeInfos = {
        data: {"result": {"minY": 343.02400000000017, "minX": 1.78860642E12, "maxY": 343.02400000000017, "series": [{"data": [[1.78860642E12, 343.02400000000017]], "isOverall": false, "label": "HTTP Request", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.78860642E12, "title": "Response Time Over Time"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average response time in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimesOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Average response time was %y ms"
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesResponseTimesOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimesOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewResponseTimesOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Response Times Over Time
function refreshResponseTimeOverTime(fixTimestamps) {
    var infos = responseTimesOverTimeInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyResponseTimeOverTime");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 19800000);
    }
    if(isGraph($("#flotResponseTimesOverTime"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesResponseTimesOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimesOverTime", "#overviewResponseTimesOverTime");
        $('#footerResponseTimesOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var latenciesOverTimeInfos = {
        data: {"result": {"minY": 336.89000000000027, "minX": 1.78860642E12, "maxY": 336.89000000000027, "series": [{"data": [[1.78860642E12, 336.89000000000027]], "isOverall": false, "label": "HTTP Request", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.78860642E12, "title": "Latencies Over Time"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average response latencies in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendLatenciesOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Average latency was %y ms"
                }
            };
        },
        createGraph: function () {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesLatenciesOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotLatenciesOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewLatenciesOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Latencies Over Time
function refreshLatenciesOverTime(fixTimestamps) {
    var infos = latenciesOverTimeInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyLatenciesOverTime");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 19800000);
    }
    if(isGraph($("#flotLatenciesOverTime"))) {
        infos.createGraph();
    }else {
        var choiceContainer = $("#choicesLatenciesOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotLatenciesOverTime", "#overviewLatenciesOverTime");
        $('#footerLatenciesOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var connectTimeOverTimeInfos = {
        data: {"result": {"minY": 0.15600000000000044, "minX": 1.78860642E12, "maxY": 0.15600000000000044, "series": [{"data": [[1.78860642E12, 0.15600000000000044]], "isOverall": false, "label": "HTTP Request", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.78860642E12, "title": "Connect Time Over Time"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getConnectTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average Connect Time in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendConnectTimeOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Average connect time was %y ms"
                }
            };
        },
        createGraph: function () {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesConnectTimeOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotConnectTimeOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewConnectTimeOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Connect Time Over Time
function refreshConnectTimeOverTime(fixTimestamps) {
    var infos = connectTimeOverTimeInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyConnectTimeOverTime");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 19800000);
    }
    if(isGraph($("#flotConnectTimeOverTime"))) {
        infos.createGraph();
    }else {
        var choiceContainer = $("#choicesConnectTimeOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotConnectTimeOverTime", "#overviewConnectTimeOverTime");
        $('#footerConnectTimeOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var responseTimePercentilesOverTimeInfos = {
        data: {"result": {"minY": 12.0, "minX": 1.78860642E12, "maxY": 5903.0, "series": [{"data": [[1.78860642E12, 5903.0]], "isOverall": false, "label": "Max", "isController": false}, {"data": [[1.78860642E12, 12.0]], "isOverall": false, "label": "Min", "isController": false}, {"data": [[1.78860642E12, 424.9]], "isOverall": false, "label": "90th percentile", "isController": false}, {"data": [[1.78860642E12, 5155.700000000001]], "isOverall": false, "label": "99th percentile", "isController": false}, {"data": [[1.78860642E12, 144.5]], "isOverall": false, "label": "Median", "isController": false}, {"data": [[1.78860642E12, 1049.7499999999682]], "isOverall": false, "label": "95th percentile", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.78860642E12, "title": "Response Time Percentiles Over Time (successful requests only)"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true,
                        fill: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Response Time in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimePercentilesOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Response time was %y ms"
                }
            };
        },
        createGraph: function () {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesResponseTimePercentilesOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimePercentilesOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewResponseTimePercentilesOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Response Time Percentiles Over Time
function refreshResponseTimePercentilesOverTime(fixTimestamps) {
    var infos = responseTimePercentilesOverTimeInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 19800000);
    }
    if(isGraph($("#flotResponseTimePercentilesOverTime"))) {
        infos.createGraph();
    }else {
        var choiceContainer = $("#choicesResponseTimePercentilesOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimePercentilesOverTime", "#overviewResponseTimePercentilesOverTime");
        $('#footerResponseTimePercentilesOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};


var responseTimeVsRequestInfos = {
    data: {"result": {"minY": 83.0, "minX": 19.0, "maxY": 3055.0, "series": [{"data": [[19.0, 3055.0], [349.0, 108.0], [190.0, 267.0], [204.0, 83.0], [238.0, 216.0]], "isOverall": false, "label": "Successes", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 1000, "maxX": 349.0, "title": "Response Time Vs Request"}},
    getOptions: function() {
        return {
            series: {
                lines: {
                    show: false
                },
                points: {
                    show: true
                }
            },
            xaxis: {
                axisLabel: "Global number of requests per second",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            yaxis: {
                axisLabel: "Median Response Time in ms",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            legend: {
                noColumns: 2,
                show: true,
                container: '#legendResponseTimeVsRequest'
            },
            selection: {
                mode: 'xy'
            },
            grid: {
                hoverable: true // IMPORTANT! this is needed for tooltip to work
            },
            tooltip: true,
            tooltipOpts: {
                content: "%s : Median response time at %x req/s was %y ms"
            },
            colors: ["#9ACD32", "#FF6347"]
        };
    },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesResponseTimeVsRequest"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotResponseTimeVsRequest"), dataset, options);
        // setup overview
        $.plot($("#overviewResponseTimeVsRequest"), dataset, prepareOverviewOptions(options));

    }
};

// Response Time vs Request
function refreshResponseTimeVsRequest() {
    var infos = responseTimeVsRequestInfos;
    prepareSeries(infos.data);
    if (isGraph($("#flotResponseTimeVsRequest"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesResponseTimeVsRequest");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimeVsRequest", "#overviewResponseTimeVsRequest");
        $('#footerResponseRimeVsRequest .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};


var latenciesVsRequestInfos = {
    data: {"result": {"minY": 77.5, "minX": 19.0, "maxY": 3025.0, "series": [{"data": [[19.0, 3025.0], [349.0, 99.0], [190.0, 260.5], [204.0, 77.5], [238.0, 206.5]], "isOverall": false, "label": "Successes", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 1000, "maxX": 349.0, "title": "Latencies Vs Request"}},
    getOptions: function() {
        return{
            series: {
                lines: {
                    show: false
                },
                points: {
                    show: true
                }
            },
            xaxis: {
                axisLabel: "Global number of requests per second",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            yaxis: {
                axisLabel: "Median Latency in ms",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            legend: { noColumns: 2,show: true, container: '#legendLatencyVsRequest' },
            selection: {
                mode: 'xy'
            },
            grid: {
                hoverable: true // IMPORTANT! this is needed for tooltip to work
            },
            tooltip: true,
            tooltipOpts: {
                content: "%s : Median Latency time at %x req/s was %y ms"
            },
            colors: ["#9ACD32", "#FF6347"]
        };
    },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesLatencyVsRequest"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotLatenciesVsRequest"), dataset, options);
        // setup overview
        $.plot($("#overviewLatenciesVsRequest"), dataset, prepareOverviewOptions(options));
    }
};

// Latencies vs Request
function refreshLatenciesVsRequest() {
        var infos = latenciesVsRequestInfos;
        prepareSeries(infos.data);
        if(isGraph($("#flotLatenciesVsRequest"))){
            infos.createGraph();
        }else{
            var choiceContainer = $("#choicesLatencyVsRequest");
            createLegend(choiceContainer, infos);
            infos.createGraph();
            setGraphZoomable("#flotLatenciesVsRequest", "#overviewLatenciesVsRequest");
            $('#footerLatenciesVsRequest .legendColorBox > div').each(function(i){
                $(this).clone().prependTo(choiceContainer.find("li").eq(i));
            });
        }
};

var hitsPerSecondInfos = {
        data: {"result": {"minY": 16.666666666666668, "minX": 1.78860642E12, "maxY": 16.666666666666668, "series": [{"data": [[1.78860642E12, 16.666666666666668]], "isOverall": false, "label": "hitsPerSecond", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.78860642E12, "title": "Hits Per Second"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of hits / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendHitsPerSecond"
                },
                selection: {
                    mode : 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y.2 hits/sec"
                }
            };
        },
        createGraph: function createGraph() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesHitsPerSecond"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotHitsPerSecond"), dataset, options);
            // setup overview
            $.plot($("#overviewHitsPerSecond"), dataset, prepareOverviewOptions(options));
        }
};

// Hits per second
function refreshHitsPerSecond(fixTimestamps) {
    var infos = hitsPerSecondInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 19800000);
    }
    if (isGraph($("#flotHitsPerSecond"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesHitsPerSecond");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotHitsPerSecond", "#overviewHitsPerSecond");
        $('#footerHitsPerSecond .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
}

var codesPerSecondInfos = {
        data: {"result": {"minY": 16.666666666666668, "minX": 1.78860642E12, "maxY": 16.666666666666668, "series": [{"data": [[1.78860642E12, 16.666666666666668]], "isOverall": false, "label": "200", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.78860642E12, "title": "Codes Per Second"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of responses / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendCodesPerSecond"
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "Number of Response Codes %s at %x was %y.2 responses / sec"
                }
            };
        },
    createGraph: function() {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesCodesPerSecond"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotCodesPerSecond"), dataset, options);
        // setup overview
        $.plot($("#overviewCodesPerSecond"), dataset, prepareOverviewOptions(options));
    }
};

// Codes per second
function refreshCodesPerSecond(fixTimestamps) {
    var infos = codesPerSecondInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 19800000);
    }
    if(isGraph($("#flotCodesPerSecond"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesCodesPerSecond");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotCodesPerSecond", "#overviewCodesPerSecond");
        $('#footerCodesPerSecond .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var transactionsPerSecondInfos = {
        data: {"result": {"minY": 16.666666666666668, "minX": 1.78860642E12, "maxY": 16.666666666666668, "series": [{"data": [[1.78860642E12, 16.666666666666668]], "isOverall": false, "label": "HTTP Request-success", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.78860642E12, "title": "Transactions Per Second"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of transactions / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendTransactionsPerSecond"
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y transactions / sec"
                }
            };
        },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesTransactionsPerSecond"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotTransactionsPerSecond"), dataset, options);
        // setup overview
        $.plot($("#overviewTransactionsPerSecond"), dataset, prepareOverviewOptions(options));
    }
};

// Transactions per second
function refreshTransactionsPerSecond(fixTimestamps) {
    var infos = transactionsPerSecondInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyTransactionsPerSecond");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 19800000);
    }
    if(isGraph($("#flotTransactionsPerSecond"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesTransactionsPerSecond");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotTransactionsPerSecond", "#overviewTransactionsPerSecond");
        $('#footerTransactionsPerSecond .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var totalTPSInfos = {
        data: {"result": {"minY": 16.666666666666668, "minX": 1.78860642E12, "maxY": 16.666666666666668, "series": [{"data": [[1.78860642E12, 16.666666666666668]], "isOverall": false, "label": "Transaction-success", "isController": false}, {"data": [], "isOverall": false, "label": "Transaction-failure", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.78860642E12, "title": "Total Transactions Per Second"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of transactions / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendTotalTPS"
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y transactions / sec"
                },
                colors: ["#9ACD32", "#FF6347"]
            };
        },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesTotalTPS"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotTotalTPS"), dataset, options);
        // setup overview
        $.plot($("#overviewTotalTPS"), dataset, prepareOverviewOptions(options));
    }
};

// Total Transactions per second
function refreshTotalTPS(fixTimestamps) {
    var infos = totalTPSInfos;
    // We want to ignore seriesFilter
    prepareSeries(infos.data, false, true);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 19800000);
    }
    if(isGraph($("#flotTotalTPS"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesTotalTPS");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotTotalTPS", "#overviewTotalTPS");
        $('#footerTotalTPS .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

// Collapse the graph matching the specified DOM element depending the collapsed
// status
function collapse(elem, collapsed){
    if(collapsed){
        $(elem).parent().find(".fa-chevron-up").removeClass("fa-chevron-up").addClass("fa-chevron-down");
    } else {
        $(elem).parent().find(".fa-chevron-down").removeClass("fa-chevron-down").addClass("fa-chevron-up");
        if (elem.id == "bodyBytesThroughputOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshBytesThroughputOverTime(true);
            }
            document.location.href="#bytesThroughputOverTime";
        } else if (elem.id == "bodyLatenciesOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshLatenciesOverTime(true);
            }
            document.location.href="#latenciesOverTime";
        } else if (elem.id == "bodyCustomGraph") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshCustomGraph(true);
            }
            document.location.href="#responseCustomGraph";
        } else if (elem.id == "bodyConnectTimeOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshConnectTimeOverTime(true);
            }
            document.location.href="#connectTimeOverTime";
        } else if (elem.id == "bodyResponseTimePercentilesOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshResponseTimePercentilesOverTime(true);
            }
            document.location.href="#responseTimePercentilesOverTime";
        } else if (elem.id == "bodyResponseTimeDistribution") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshResponseTimeDistribution();
            }
            document.location.href="#responseTimeDistribution" ;
        } else if (elem.id == "bodySyntheticResponseTimeDistribution") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshSyntheticResponseTimeDistribution();
            }
            document.location.href="#syntheticResponseTimeDistribution" ;
        } else if (elem.id == "bodyActiveThreadsOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshActiveThreadsOverTime(true);
            }
            document.location.href="#activeThreadsOverTime";
        } else if (elem.id == "bodyTimeVsThreads") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshTimeVsThreads();
            }
            document.location.href="#timeVsThreads" ;
        } else if (elem.id == "bodyCodesPerSecond") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshCodesPerSecond(true);
            }
            document.location.href="#codesPerSecond";
        } else if (elem.id == "bodyTransactionsPerSecond") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshTransactionsPerSecond(true);
            }
            document.location.href="#transactionsPerSecond";
        } else if (elem.id == "bodyTotalTPS") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshTotalTPS(true);
            }
            document.location.href="#totalTPS";
        } else if (elem.id == "bodyResponseTimeVsRequest") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshResponseTimeVsRequest();
            }
            document.location.href="#responseTimeVsRequest";
        } else if (elem.id == "bodyLatenciesVsRequest") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshLatenciesVsRequest();
            }
            document.location.href="#latencyVsRequest";
        }
    }
}

/*
 * Activates or deactivates all series of the specified graph (represented by id parameter)
 * depending on checked argument.
 */
function toggleAll(id, checked){
    var placeholder = document.getElementById(id);

    var cases = $(placeholder).find(':checkbox');
    cases.prop('checked', checked);
    $(cases).parent().children().children().toggleClass("legend-disabled", !checked);

    var choiceContainer;
    if ( id == "choicesBytesThroughputOverTime"){
        choiceContainer = $("#choicesBytesThroughputOverTime");
        refreshBytesThroughputOverTime(false);
    } else if(id == "choicesResponseTimesOverTime"){
        choiceContainer = $("#choicesResponseTimesOverTime");
        refreshResponseTimeOverTime(false);
    }else if(id == "choicesResponseCustomGraph"){
        choiceContainer = $("#choicesResponseCustomGraph");
        refreshCustomGraph(false);
    } else if ( id == "choicesLatenciesOverTime"){
        choiceContainer = $("#choicesLatenciesOverTime");
        refreshLatenciesOverTime(false);
    } else if ( id == "choicesConnectTimeOverTime"){
        choiceContainer = $("#choicesConnectTimeOverTime");
        refreshConnectTimeOverTime(false);
    } else if ( id == "choicesResponseTimePercentilesOverTime"){
        choiceContainer = $("#choicesResponseTimePercentilesOverTime");
        refreshResponseTimePercentilesOverTime(false);
    } else if ( id == "choicesResponseTimePercentiles"){
        choiceContainer = $("#choicesResponseTimePercentiles");
        refreshResponseTimePercentiles();
    } else if(id == "choicesActiveThreadsOverTime"){
        choiceContainer = $("#choicesActiveThreadsOverTime");
        refreshActiveThreadsOverTime(false);
    } else if ( id == "choicesTimeVsThreads"){
        choiceContainer = $("#choicesTimeVsThreads");
        refreshTimeVsThreads();
    } else if ( id == "choicesSyntheticResponseTimeDistribution"){
        choiceContainer = $("#choicesSyntheticResponseTimeDistribution");
        refreshSyntheticResponseTimeDistribution();
    } else if ( id == "choicesResponseTimeDistribution"){
        choiceContainer = $("#choicesResponseTimeDistribution");
        refreshResponseTimeDistribution();
    } else if ( id == "choicesHitsPerSecond"){
        choiceContainer = $("#choicesHitsPerSecond");
        refreshHitsPerSecond(false);
    } else if(id == "choicesCodesPerSecond"){
        choiceContainer = $("#choicesCodesPerSecond");
        refreshCodesPerSecond(false);
    } else if ( id == "choicesTransactionsPerSecond"){
        choiceContainer = $("#choicesTransactionsPerSecond");
        refreshTransactionsPerSecond(false);
    } else if ( id == "choicesTotalTPS"){
        choiceContainer = $("#choicesTotalTPS");
        refreshTotalTPS(false);
    } else if ( id == "choicesResponseTimeVsRequest"){
        choiceContainer = $("#choicesResponseTimeVsRequest");
        refreshResponseTimeVsRequest();
    } else if ( id == "choicesLatencyVsRequest"){
        choiceContainer = $("#choicesLatencyVsRequest");
        refreshLatenciesVsRequest();
    }
    var color = checked ? "black" : "#818181";
    if(choiceContainer != null) {
        choiceContainer.find("label").each(function(){
            this.style.color = color;
        });
    }
}

