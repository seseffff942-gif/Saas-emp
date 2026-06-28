const { createClient } = require('./web-version/node_modules/@supabase/supabase-js');
const fs = require('fs');
require('./web-version/node_modules/dotenv').config({ path: 'web-version/.env.local' });

const supabaseUrl = process.env.NEXT_PUBLIC_SUPABASE_URL;
const supabaseKey = process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY;
const supabase = createClient(supabaseUrl, supabaseKey);

async function test() {
    const { data } = supabase.storage.from('products').getPublicUrl('test.jpg');
    console.log(data);
}
test();
