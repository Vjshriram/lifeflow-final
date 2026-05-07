<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Find Blood Bank | LifeFlow</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/assets/css/theme.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        .locator-header {
            padding: 10rem 0 5rem;
            background: linear-gradient(180deg, #020617 0%, var(--bg-dark) 100%);
            text-align: center;
        }
        .accent-pill {
            background: rgba(225, 29, 72, 0.15);
            color: var(--primary-crimson);
            border-radius: var(--radius-pill);
            padding: 0.5rem 1.2rem;
            font-size: 0.8rem;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 1px;
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
        }
        .search-card {
            background: var(--surface-dark);
            border: var(--border-glass);
            border-radius: var(--radius-lg);
            padding: 3rem;
            margin-top: -4rem;
            position: relative;
            z-index: 10;
        }
        .form-label-premium {
            color: var(--text-secondary);
            font-weight: 600;
            font-size: 0.85rem;
            text-transform: uppercase;
            letter-spacing: 1px;
            margin-bottom: 0.75rem;
            display: block;
        }
        .input-group-premium {
            background: rgba(255, 255, 255, 0.03);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 12px;
            padding: 0.75rem 1rem;
            display: flex;
            align-items: center;
            gap: 1rem;
            transition: var(--transition-premium);
        }
        .input-group-premium:focus-within {
            border-color: var(--primary-crimson);
            background: rgba(255, 255, 255, 0.05);
            box-shadow: 0 0 0 4px rgba(225, 29, 72, 0.1);
        }
        .custom-select-dark {
            background: transparent;
            border: none;
            color: white;
            width: 100%;
            outline: none;
            cursor: pointer;
            font-weight: 600;
        }
        .custom-select-dark option {
            background: var(--bg-dark);
            color: white;
        }
        .results-table {
            background: var(--surface-dark);
            border: var(--border-glass);
            border-radius: var(--radius-lg);
            overflow: hidden;
            margin-top: 3rem;
        }
        .table-dark-premium {
            margin-bottom: 0;
            color: var(--text-primary);
            --bs-table-bg: transparent;
            --bs-table-color: var(--text-primary);
        }
        .table-dark-premium thead th {
            background: rgba(255, 255, 255, 0.02);
            border: none;
            padding: 1.5rem;
            text-transform: uppercase;
            font-size: 0.75rem;
            letter-spacing: 1px;
            color: var(--text-secondary);
        }
        .table-dark-premium tbody td {
            padding: 1.5rem;
            border-top: 1px solid rgba(255, 255, 255, 0.05);
            vertical-align: middle;
        }
        .distance-badge {
            background: rgba(225, 29, 72, 0.1);
            color: var(--primary-crimson);
            padding: 0.4rem 1rem;
            border-radius: var(--radius-pill);
            font-weight: 700;
            font-size: 0.85rem;
        }
    </style>
</head>
<body>

<%@ include file="/WEB-INF/fragments/header.jspf" %>

<div class="locator-header">
    <div class="container fade-in-up">
        <div class="accent-pill mb-4">
            <i class="fa-solid fa-location-dot"></i> <fmt:message key="nav.locator_tag" />
        </div>
        <h1 class="display-3 fw-bold text-white mb-4" style="font-family: 'Poppins';"><fmt:message key="nav.locator_title" /></h1>
        <p class="text-secondary fs-5 mx-auto" style="max-width: 650px;"><fmt:message key="nav.locator_subtitle" /></p>
    </div>
</div>

<main class="container mb-5 pb-5">
    <div class="search-card fade-in-up delay-100">
        <div class="d-flex justify-content-between align-items-center mb-5">
            <h4 class="text-white fw-bold mb-0"><i class="fa-solid fa-magnifying-glass me-2 text-danger"></i> <fmt:message key="nav.search_criteria" /></h4>
            <button id="btnUseLocation" class="btn btn-outline-premium rounded-pill px-4">
                <i class="fa-solid fa-location-crosshairs me-2"></i> <fmt:message key="nav.use_gps" />
            </button>
        </div>

        <form id="searchForm" class="row g-4 align-items-end">
            <div class="col-lg-4">
                <label class="form-label-premium">State</label>
                <div class="input-group-premium">
                    <i class="fa-solid fa-map text-secondary"></i>
                    <select class="custom-select-dark" id="stateSelect">
                        <option value="" selected disabled>Select State</option>
                    </select>
                </div>
            </div>
            <div class="col-lg-4">
                <label class="form-label-premium">District</label>
                <div class="input-group-premium">
                    <i class="fa-solid fa-city text-secondary"></i>
                    <select class="custom-select-dark" id="city">
                        <option value="" selected disabled>Select District</option>
                    </select>
                </div>
            </div>
            <div class="col-lg-4">
                <label class="form-label-premium">Blood Group</label>
                <div class="input-group-premium">
                    <i class="fa-solid fa-droplet text-danger"></i>
                    <select id="bloodGroup" class="custom-select-dark">
                        <option value="">Any Available</option>
                        <option value="A+">A+</option>
                        <option value="A-">A-</option>
                        <option value="B+">B+</option>
                        <option value="B-">B-</option>
                        <option value="O+">O+</option>
                        <option value="O-">O-</option>
                        <option value="AB+">AB+</option>
                        <option value="AB-">AB-</option>
                    </select>
                </div>
            </div>
            <div class="col-12 text-end mt-5">
                <button type="button" id="btnSearch" class="btn btn-join py-3 px-5">
                    <fmt:message key="nav.search_facilities" /> <i class="fa-solid fa-arrow-right ms-2"></i>
                </button>
            </div>
        </form>
    </div>

    <div class="results-table fade-in-up delay-200">
        <div class="table-responsive">
            <table class="table table-dark table-hover table-dark-premium align-middle">
                <thead>
                    <tr>
                        <th>Facility Details</th>
                        <th>Location</th>
                        <th>Est. Distance</th>
                        <th class="text-center">Action</th>
                    </tr>
                </thead>
                <tbody id="resultsBody">
                    <tr>
                        <td colspan="4" class="text-center text-secondary py-5">
                            <i class="fa-solid fa-map-location-dot fs-1 mb-3 opacity-25"></i><br>
                            Start searching by city, district, or your device GPS.
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/fragments/footer.jspf" %>

<script>
    const apiBase = '<%= request.getContextPath() %>/api/locator';

    function createDirectionsUrl(lat, lng, label) {
        if (lat && lng && lat !== 0 && lng !== 0) {
            return `https://www.google.com/maps/dir/?api=1&destination=${lat},${lng}`;
        } else {
            return `https://www.google.com/maps/dir/?api=1&destination=${encodeURIComponent(label)}`;
        }
    }

    function renderResults(banks) {
        const body = document.getElementById('resultsBody');
        body.innerHTML = '';

        if (!banks || banks.length === 0) {
            body.innerHTML = '<tr><td colspan="4" class="text-center text-secondary py-5">No facilities found matching your criteria.</td></tr>';
            return;
        }

        banks.forEach(bank => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>
                    <div class="fw-bold text-danger mb-1" style="font-size: 1.1rem;">\${bank.name}</div>
                    <div class="text-secondary small"><i class="fa-solid fa-id-badge me-1"></i> ID #\${bank.id}</div>
                </td>
                <td>
                    <div class="text-white small mb-1">\${bank.addressLine1 || '-'}</div>
                    <div class="text-secondary small">\${bank.city || ''}</div>
                </td>
                <td>
                    <span class="distance-badge">
                        <i class="fa-solid fa-route me-1"></i> \${bank.distanceKm.toFixed(1)} km
                    </span>
                </td>
                <td class="text-center">
                    <a href="\${createDirectionsUrl(bank.latitude, bank.longitude, bank.name)}" target="_blank" class="btn btn-sm btn-outline-light rounded-pill px-3 me-2">Maps</a>
                    <button class="btn btn-sm btn-join rounded-pill px-3" onclick="onBookAppointment(\${bank.id})">Book</button>
                </td>
            `;
            body.appendChild(row);
        });
    }

    function onBookAppointment(bankId) {
        window.location.href = '<%=request.getContextPath()%>/BookAppointmentServlet?prefillBankId=' + bankId;
    }

    async function searchByLocation(lat, lng) {
        const bloodGroup = document.getElementById('bloodGroup').value;
        const url = new URL(apiBase, window.location.origin);
        url.searchParams.set('lat', lat);
        url.searchParams.set('lng', lng);
        url.searchParams.set('radiusKm', '50');
        if (bloodGroup) url.searchParams.set('bloodGroup', bloodGroup);

        setLoading();
        try {
            const resp = await fetch(url.toString());
            const data = await resp.json();
            renderResults(data.banks || []);
        } catch (e) {
            setError();
        }
    }

    document.getElementById('btnUseLocation').addEventListener('click', () => {
        navigator.geolocation.getCurrentPosition(
            (pos) => searchByLocation(pos.coords.latitude, pos.coords.longitude),
            () => alert('Unable to access location.')
        );
    });

    document.getElementById('btnSearch').addEventListener('click', async (e) => {
        const city = document.getElementById('city').value;
        if(!city) return alert('Please select a district.');
        
        const btn = e.currentTarget;
        const originalHtml = btn.innerHTML;
        btn.disabled = true;
        btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span> Searching...';

        const bloodGroup = document.getElementById('bloodGroup').value;
        const url = new URL(apiBase, window.location.origin);
        url.searchParams.set('city', city);
        if (bloodGroup) url.searchParams.set('bloodGroup', bloodGroup);

        setLoading();
        try {
            const resp = await fetch(url.toString());
            const data = await resp.json();
            renderResults(data.banks || []);
        } catch (e) {
            setError();
        } finally {
            btn.disabled = false;
            btn.innerHTML = originalHtml;
        }
    });

    function setLoading() {
        document.getElementById('resultsBody').innerHTML = '<tr><td colspan="4" class="text-center py-5"><div class="spinner-border text-danger"></div></td></tr>';
    }
    function setError() {
        document.getElementById('resultsBody').innerHTML = '<tr><td colspan="4" class="text-center text-danger py-5">Network error.</td></tr>';
    }

    // Geodata - hardcoded inline (no fetch needed, guaranteed to work)
    (function initGeodata() {
        const stateSelect = document.getElementById("stateSelect");
        const citySelect = document.getElementById("city");
        
        if (!stateSelect || !citySelect) return;

        const data = {  
           "states":[  
              {"state":"Andhra Pradesh","districts":["Anantapur","Chittoor","East Godavari","Guntur","Krishna","Kurnool","Nellore","Prakasam","Srikakulam","Visakhapatnam","Vizianagaram","West Godavari","YSR Kadapa"]},
              {"state":"Arunachal Pradesh","districts":["Tawang","West Kameng","East Kameng","Papum Pare","Kurung Kumey","Kra Daadi","Lower Subansiri","Upper Subansiri","West Siang","East Siang","Siang","Upper Siang","Lower Siang","Lower Dibang Valley","Dibang Valley","Anjaw","Lohit","Namsai","Changlang","Tirap","Longding"]},
              {"state":"Assam","districts":["Baksa","Barpeta","Biswanath","Bongaigaon","Cachar","Charaideo","Chirang","Darrang","Dhemaji","Dhubri","Dibrugarh","Goalpara","Golaghat","Hailakandi","Hojai","Jorhat","Kamrup Metropolitan","Kamrup","Karbi Anglong","Karimganj","Kokrajhar","Lakhimpur","Majuli","Morigaon","Nagaon","Nalbari","Dima Hasao","Sivasagar","Sonitpur","South Salmara-Mankachar","Tinsukia","Udalguri","West Karbi Anglong"]},
              {"state":"Bihar","districts":["Araria","Arwal","Aurangabad","Banka","Begusarai","Bhagalpur","Bhojpur","Buxar","Darbhanga","East Champaran (Motihari)","Gaya","Gopalganj","Jamui","Jehanabad","Kaimur (Bhabua)","Katihar","Khagaria","Kishanganj","Lakhisarai","Madhepura","Madhubani","Munger (Monghyr)","Muzaffarpur","Nalanda","Nawada","Patna","Purnia (Purnea)","Rohtas","Saharsa","Samastipur","Saran","Sheikhpura","Sheohar","Sitamarhi","Siwan","Supaul","Vaishali","West Champaran"]},
              {"state":"Chandigarh (UT)","districts":["Chandigarh"]},
              {"state":"Chhattisgarh","districts":["Balod","Baloda Bazar","Balrampur","Bastar","Bemetara","Bijapur","Bilaspur","Dantewada (South Bastar)","Dhamtari","Durg","Gariyaband","Janjgir-Champa","Jashpur","Kabirdham (Kawardha)","Kanker (North Bastar)","Kondagaon","Korba","Korea (Koriya)","Mahasamund","Mungeli","Narayanpur","Raigarh","Raipur","Rajnandgaon","Sukma","Surajpur  ","Surguja"]},
              {"state":"Dadra and Nagar Haveli (UT)","districts":["Dadra & Nagar Haveli"]},
              {"state":"Daman and Diu (UT)","districts":["Daman","Diu"]},
              {"state":"Delhi (NCT)","districts":["Central Delhi","East Delhi","New Delhi","North Delhi","North East  Delhi","North West  Delhi","Shahdara","South Delhi","South East Delhi","South West  Delhi","West Delhi"]},
              {"state":"Goa","districts":["North Goa","South Goa"]},
              {"state":"Gujarat","districts":["Ahmedabad","Amreli","Anand","Aravalli","Banaskantha (Palanpur)","Bharuch","Bhavnagar","Botad","Chhota Udepur","Dahod","Dangs (Ahwa)","Devbhoomi Dwarka","Gandhinagar","Gir Somnath","Jamnagar","Junagadh","Kachchh","Kheda (Nadiad)","Mahisagar","Mehsana","Morbi","Narmada (Rajpipla)","Navsari","Panchmahal (Godhra)","Patan","Porbandar","Rajkot","Sabarkantha (Himmatnagar)","Surat","Surendranagar","Tapi (Vyara)","Vadodara","Valsad"]},
              {"state":"Haryana","districts":["Ambala","Bhiwani","Charkhi Dadri","Faridabad","Fatehabad","Gurgaon","Hisar","Jhajjar","Jind","Kaithal","Karnal","Kurukshetra","Mahendragarh","Mewat","Palwal","Panchkula","Panipat","Rewari","Rohtak","Sirsa","Sonipat","Yamunanagar"]},
              {"state":"Himachal Pradesh","districts":["Bilaspur","Chamba","Hamirpur","Kangra","Kinnaur","Kullu","Lahaul &amp; Spiti","Mandi","Shimla","Sirmaur (Sirmour)","Solan","Una"]},
              {"state":"Jammu and Kashmir","districts":["Anantnag","Bandipore","Baramulla","Budgam","Doda","Ganderbal","Jammu","Kargil","Kathua","Kishtwar","Kulgam","Kupwara","Leh","Poonch","Pulwama","Rajouri","Ramban","Reasi","Samba","Shopian","Srinagar","Udhampur"]},
              {"state":"Jharkhand","districts":["Bokaro","Chatra","Deoghar","Dhanbad","Dumka","East Singhbhum","Garhwa","Giridih","Godda","Gumla","Hazaribag","Jamtara","Khunti","Koderma","Latehar","Lohardaga","Pakur","Palamu","Ramgarh","Ranchi","Sahibganj","Seraikela-Kharsawan","Simdega","West Singhbhum"]},
              {"state":"Karnataka","districts":["Bagalkot","Ballari (Bellary)","Belagavi (Belgaum)","Bengaluru (Bangalore) Rural","Bengaluru (Bangalore) Urban","Bidar","Chamarajanagar","Chikballapur","Chikkamagaluru (Chikmagalur)","Chitradurga","Dakshina Kannada","Davangere","Dharwad","Gadag","Hassan","Haveri","Kalaburagi (Gulbarga)","Kodagu","Kolar","Koppal","Mandya","Mysuru (Mysore)","Raichur","Ramanagara","Shivamogga (Shimoga)","Tumakuru (Tumkur)","Udupi","Uttara Kannada (Karwar)","Vijayapura (Bijapur)","Yadgir"]},
              {"state":"Kerala","districts":["Alappuzha","Ernakulam","Idukki","Kannur","Kasaragod","Kollam","Kottayam","Kozhikode","Malappuram","Palakkad","Pathanamthitta","Thiruvananthapuram","Thrissur","Wayanad"]},
              {"state":"Lakshadweep (UT)","districts":["Agatti","Amini","Androth","Bithra","Chethlath","Kavaratti","Kadmath","Kalpeni","Kilthan","Minicoy"]},
              {"state":"Madhya Pradesh","districts":["Agar Malwa","Alirajpur","Anuppur","Ashoknagar","Balaghat","Barwani","Betul","Bhind","Bhopal","Burhanpur","Chhatarpur","Chhindwara","Damoh","Datia","Dewas","Dhar","Dindori","Guna","Gwalior","Harda","Hoshangabad","Indore","Jabalpur","Jhabua","Katni","Khandwa","Khargone","Mandla","Mandsaur","Morena","Narsinghpur","Neemuch","Panna","Raisen","Rajgarh","Ratlam","Rewa","Sagar","Satna","Sehore","Seoni","Shahdol","Shajapur","Sheopur","Shivpuri","Sidhi","Singrauli","Tikamgarh","Ujjain","Umaria","Vidisha"]},
              {"state":"Maharashtra","districts":["Ahmednagar","Akola","Amravati","Aurangabad","Beed","Bhandara","Buldhana","Chandrapur","Dhule","Gadchiroli","Gondia","Hingoli","Jalgaon","Jalna","Kolhapur","Latur","Mumbai City","Mumbai Suburban","Nagpur","Nanded","Nandurbar","Nashik","Osmanabad","Palghar","Parbhani","Pune","Raigad","Ratnagiri","Sangli","Satara","Sindhudurg","Solapur","Thane","Wardha","Washim","Yavatmal"]},
              {"state":"Manipur","districts":["Bishnupur","Chandel","Churachandpur","Imphal East","Imphal West","Jiribam","Kakching","Kamjong","Kangpokpi","Noney","Pherzawl","Senapati","Tamenglong","Tengnoupal","Thoubal","Ukhrul"]},
              {"state":"Meghalaya","districts":["East Garo Hills","East Jaintia Hills","East Khasi Hills","North Garo Hills","Ri Bhoi","South Garo Hills","South West Garo Hills ","South West Khasi Hills","West Garo Hills","West Jaintia Hills","West Khasi Hills"]},
              {"state":"Mizoram","districts":["Aizawl","Champhai","Kolasib","Lawngtlai","Lunglei","Mamit","Saiha","Serchhip"]},
              {"state":"Nagaland","districts":["Dimapur","Kiphire","Kohima","Longleng","Mokokchung","Mon","Peren","Phek","Tuensang","Wokha","Zunheboto"]},
              {"state":"Odisha","districts":["Angul","Balangir","Balasore","Bargarh","Bhadrak","Boudh","Cuttack","Deogarh","Dhenkanal","Gajapati","Ganjam","Jagatsinghapur","Jajpur","Jharsuguda","Kalahandi","Kandhamal","Kendrapara","Kendujhar (Keonjhar)","Khordha","Koraput","Malkangiri","Mayurbhanj","Nabarangpur","Nayagarh","Nuapada","Puri","Rayagada","Sambalpur","Sonepur","Sundargarh"]},
              {"state":"Puducherry (UT)","districts":["Karaikal","Mahe","Pondicherry","Yanam"]},
              {"state":"Punjab","districts":["Amritsar","Barnala","Bathinda","Faridkot","Fatehgarh Sahib","Fazilka","Ferozepur","Gurdaspur","Hoshiarpur","Jalandhar","Kapurthala","Ludhiana","Mansa","Moga","Muktsar","Nawanshahr (Shahid Bhagat Singh Nagar)","Pathankot","Patiala","Rupnagar","Sahibzada Ajit Singh Nagar (Mohali)","Sangrur","Tarn Taran"]},
              {"state":"Rajasthan","districts":["Ajmer","Alwar","Banswara","Baran","Barmer","Bharatpur","Bhilwara","Bikaner","Bundi","Chittorgarh","Churu","Dausa","Dholpur","Dungarpur","Hanumangarh","Jaipur","Jaisalmer","Jalore","Jhalawar","Jhunjhunu","Jodhpur","Karauli","Kota","Nagaur","Pali","Pratapgarh","Rajsamand","Sawai Madhopur","Sikar","Sirohi","Sri Ganganagar","Tonk","Udaipur"]},
              {"state":"Sikkim","districts":["East Sikkim","North Sikkim","South Sikkim","West Sikkim"]},
              {"state":"Tamil Nadu","districts":["Ariyalur","Chennai","Coimbatore","Cuddalore","Dharmapuri","Dindigul","Erode","Kanchipuram","Kanyakumari","Karur","Krishnagiri","Madurai","Nagapattinam","Namakkal","Nilgiris","Perambalur","Pudukkottai","Ramanathapuram","Salem","Sivaganga","Thanjavur","Theni","Thoothukudi (Tuticorin)","Tiruchirappalli","Tirunelveli","Tiruppur","Tiruvallur","Tiruvannamalai","Tiruvarur","Vellore","Viluppuram","Virudhunagar"]},
              {"state":"Telangana","districts":["Adilabad","Bhadradri Kothagudem","Hyderabad","Jagtial","Jangaon","Jayashankar Bhoopalpally","Jogulamba Gadwal","Kamareddy","Karimnagar","Khammam","Komaram Bheem Asifabad","Mahabubabad","Mahabubnagar","Mancherial","Medak","Medchal","Nagarkurnool","Nalgonda","Nirmal","Nizamabad","Peddapalli","Rajanna Sircilla","Rangareddy","Sangareddy","Siddipet","Suryapet","Vikarabad","Wanaparthy","Warangal (Rural)","Warangal (Urban)","Yadadri Bhuvanagiri"]},
              {"state":"Tripura","districts":["Dhalai","Gomati","Khowai","North Tripura","Sepahijala","South Tripura","Unakoti","West Tripura"]},
              {"state":"Uttarakhand","districts":["Almora","Bageshwar","Chamoli","Champawat","Dehradun","Haridwar","Nainital","Pauri Garhwal","Pithoragarh","Rudraprayag","Tehri Garhwal","Udham Singh Nagar","Uttarkashi"]},
              {"state":"Uttar Pradesh","districts":["Agra","Aligarh","Allahabad","Ambedkar Nagar","Amethi (Chatrapati Sahuji Maharaj Nagar)","Amroha (J.P. Nagar)","Auraiya","Azamgarh","Baghpat","Bahraich","Ballia","Balrampur","Banda","Barabanki","Bareilly","Basti","Bhadohi","Bijnor","Budaun","Bulandshahr","Chandauli","Chitrakoot","Deoria","Etah","Etawah","Faizabad","Farrukhabad","Fatehpur","Firozabad","Gautam Buddha Nagar","Ghaziabad","Ghazipur","Gonda","Gorakhpur","Hamirpur","Hapur (Panchsheel Nagar)","Hardoi","Hathras","Jalaun","Jaunpur","Jhansi","Kannauj","Kanpur Dehat","Kanpur Nagar","Kasganj (Kanshiram Nagar)","Kaushambi","Kushinagar (Padrauna)","Lakhimpur - Kheri","Lalitpur","Lucknow","Maharajganj","Mahoba","Mainpuri","Mathura","Mau","Meerut","Mirzapur","Moradabad","Muzaffarnagar","Pilibhit","Pratapgarh","RaeBareli","Rampur","Saharanpur","Sambhal (Bhim Nagar)","Sant Kabir Nagar","Shahjahanpur","Shamli (Prabuddha Nagar)","Shravasti","Siddharthnagar","Sitapur","Sonbhadra","Sultanpur","Unnao","Varanasi"]},
              {"state":"West Bengal","districts":["Alipurduar","Bankura","Birbhum","Burdwan (Bardhaman)","Cooch Behar","Dakshin Dinajpur (South Dinajpur)","Darjeeling","Hooghly","Howrah","Jalpaiguri","Kalimpong","Kolkata","Malda","Murshidabad","Nadia","North 24 Parganas","Paschim Medinipur (West Midnapore)","Purba Medinipur (East Midnapore)","Purulia","South 24 Parganas","Uttar Dinajpur (North Dinajpur)"]}
           ]
        };

        // Populate State Select
        stateSelect.innerHTML = '<option value="" selected disabled>Select State</option>';
        data.states.forEach(state => {
            const opt = document.createElement("option");
            opt.value = state.state;
            opt.textContent = state.state;
            stateSelect.appendChild(opt);
        });

        // Populate City/District Select based on State
        stateSelect.addEventListener("change", () => {
            citySelect.innerHTML = '<option value="" selected disabled>Select District</option>';
            const s = data.states.find(x => x.state === stateSelect.value);
            if (s && s.districts) {
                s.districts.forEach(d => {
                    const opt = document.createElement("option");
                    opt.value = d;
                    opt.textContent = d;
                    citySelect.appendChild(opt);
                });
            }
        });
    })();
</script>

</body>
</html>
