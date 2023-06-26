package hackvertor;

import burp.IHttpRequestResponse;
import burp.IRequestInfo;
import burp.api.montoya.http.handler.HttpRequestToBeSent;
import hackvertor.parser.Element;
import hackvertor.parser.HackvertorParser;
import hackvertor.parser.ParseException;
import com.github.javafaker.Faker;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

public class Hackvertor {
    private ArrayList<Tag> tags = new ArrayList<Tag>();
    private JSONArray customTags = new JSONArray();
    private IRequestInfo analyzedRequest = null;
    private String montoyaRequest = null;
    HttpRequestToBeSent montoyaHttpRequestObject = null;
    private byte[] request;
    public Hackvertor(){
        init();
    }

    public void analyzeRequest(byte[] request, IHttpRequestResponse messageInfo) {
        this.request = request;
        this.analyzedRequest = HackvertorExtension.helpers.analyzeRequest(messageInfo.getHttpService(), request);
    }

    public void setMontoyaRequest(String request, HttpRequestToBeSent requestToBeSent) {
        montoyaRequest = request;
        montoyaHttpRequestObject = requestToBeSent;
    }

    public byte[] getRequest() {
        return request;
    }

    public IRequestInfo getAnalyzedRequest() {
        return analyzedRequest;
    }

    public String getMontoyaRequest() { return montoyaRequest; }

    public HttpRequestToBeSent getMontoyaHttpObject() {
        return montoyaHttpRequestObject;
    }

    public void setRequest(byte[] request) {
        this.request = request;
    }

    public static String removeHackvertorTags(String input) {
        try {
            input = HackvertorParser.parse(input).stream()
                    .filter(element -> element instanceof Element.TextElement)
                    .map(element -> ((Element.TextElement) element).getContent())
                    .collect(Collectors.joining());
        }catch (ParseException ex){
            //TODO Better error handling.
            ex.printStackTrace();
        }
        return input;
    }

    void init() {
        Tag tag;
        SortedMap m = Charset.availableCharsets();
        Set k = m.keySet();
        Iterator i = k.iterator();
        while (i.hasNext()) {
            String n = (String) i.next();
            Charset e = (Charset) m.get(n);
            String d = e.displayName();
            boolean c = e.canEncode();
            if (!c) {
                continue;
            }
            Set s = e.aliases();
            Iterator j = s.iterator();
            while (j.hasNext()) {
                String a = (String) j.next();
                tags.add(new Tag(Tag.Category.Charsets, a, true, a + "(String input)"));
            }
        }

        tag = new Tag(Tag.Category.Charsets, "charset_convert", true, "charset_convert(String input, String from, String to)");
        tag.argument1 = new TagArgument("string", "from");
        tag.argument2 = new TagArgument("string", "to");
        tags.add(tag);
        tag = new Tag(Tag.Category.Charsets, "utf7", true, "utf7(String str, String excludeCharacters)");
        tag.argument1 = new TagArgument("string", "\\s\\t\\r'(),-./:?ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789=+!");
        tags.add(tag);
        tags.add(new Tag(Tag.Category.Compression, "brotli_decompress", true, "brotli_decompress(String str)"));
        tags.add(new Tag(Tag.Category.Compression, "gzip_compress", true, "gzip_compress(String str)"));
        tags.add(new Tag(Tag.Category.Compression, "gzip_decompress", true, "gzip_decompress(String str)"));
        tags.add(new Tag(Tag.Category.Compression, "bzip2_compress", true, "bzip2_compress(String str)"));
        tags.add(new Tag(Tag.Category.Compression, "bzip2_decompress", true, "bzip2_decompress(String str)"));
        tag = new Tag(Tag.Category.Compression, "deflate_compress", true, "deflate_compress(String str, Boolean includeHeader)");
        tag.argument1 = new TagArgument("boolean", "true");
        tags.add(tag);
        tag = new Tag(Tag.Category.Compression, "deflate_decompress", true, "deflate_decompress(String str, Boolean includeHeader)");
        tag.argument1 = new TagArgument("boolean", "true");
        tags.add(tag);
        tags.add(new Tag(Tag.Category.Date, "timestamp", false, "timestamp()"));
        tag = new Tag(Tag.Category.Date, "date", false, "date(String format)");
        tag.argument1 = new TagArgument("string", "yyyy-MM-dd HH:mm:ss");
        tag.argument2 = new TagArgument("string", "GMT");
        tags.add(tag);
        tag = new Tag(Tag.Category.Encrypt, "aes_encrypt", true, "aes_encrypt(String plaintext, String key, String transformations)");
        tag.argument1 = new TagArgument("string", "supersecret12356");
        tag.argument2 = new TagArgument("string", "AES/ECB/PKCS5PADDING");
        tag.argument3 = new TagArgument("string", "initVector123456");
        tags.add(tag);
        tag = new Tag(Tag.Category.Decrypt, "aes_decrypt", true, "aes_decrypt(String ciphertext, String key, String transformations)");
        tag.argument1 = new TagArgument("string", "supersecret12356");
        tag.argument2 = new TagArgument("string", "AES/ECB/PKCS5PADDING");
        tag.argument3 = new TagArgument("string", "initVector123456");
        tags.add(tag);
        tag = new Tag(Tag.Category.Encrypt, "rotN", true, "rotN(String str, int n)");
        tag.argument1 = new TagArgument("int", "13");
        tags.add(tag);
        tag = new Tag(Tag.Category.Encrypt, "xor", true, "xor(String message, String key)");
        tag.argument1 = new TagArgument("string", "key");
        tags.add(tag);
        tag = new Tag(Tag.Category.Decrypt, "xor_decrypt", true, "xor_decrypt(String ciphertext, int keyLength)");
        tag.argument1 = new TagArgument("int", "3");
        tags.add(tag);
        tags.add(new Tag(Tag.Category.Encrypt, "xor_getkey", true, "xor_getkey(String ciphertext)"));
        tag = new Tag(Tag.Category.Encrypt, "affine_encrypt", true, "affine_encrypt(String message, int key1, int key2)");
        tag.argument1 = new TagArgument("int", "5");
        tag.argument2 = new TagArgument("int", "9");
        tags.add(tag);
        tag = new Tag(Tag.Category.Decrypt, "affine_decrypt", true, "affine_decrypt(String ciphertext, int key1, int key2)");
        tag.argument1 = new TagArgument("int", "5");
        tag.argument2 = new TagArgument("int", "9");
        tags.add(tag);
        tags.add(new Tag(Tag.Category.Encrypt, "atbash_encrypt", true, "atbash_encrypt(String message)"));
        tags.add(new Tag(Tag.Category.Decrypt, "atbash_decrypt", true, "atbash_decrypt(String ciphertext)"));
        tags.add(new Tag(Tag.Category.Encrypt, "rotN_bruteforce", true, "rotN_bruteforce(String str)"));
        tag = new Tag(Tag.Category.Encrypt, "rail_fence_encrypt", true, "rail_fence_encrypt(String message, int key)");
        tag.argument1 = new TagArgument("int", "4");
        tags.add(tag);
        tag = new Tag(Tag.Category.Decrypt, "rail_fence_decrypt", true, "rail_fence_decrypt(String encoded, int key)");
        tag.argument1 = new TagArgument("int", "4");
        tags.add(tag);
        tag = new Tag(Tag.Category.Encrypt, "substitution_encrypt", true, "substitution_encrypt(String message, String key)");
        tag.argument1 = new TagArgument("string", "phqgiumeaylnofdxjkrcvstzwb");
        tags.add(tag);
        tag = new Tag(Tag.Category.Decrypt, "substitution_decrypt", true, "substitution_decrypt(String ciphertext, String key)");
        tag.argument1 = new TagArgument("string", "phqgiumeaylnofdxjkrcvstzwb");
        tags.add(tag);
        tags.add(new Tag(Tag.Category.Encrypt, "is_like_english", true, "is_like_english(String str)"));
        tags.add(new Tag(Tag.Category.Encrypt, "index_of_coincidence", true, "index_of_coincidence(String str)"));
        tags.add(new Tag(Tag.Category.Encrypt, "guess_key_length", true, "guess_key_length(String ciphertext)"));
        tags.add(new Tag(Tag.Category.Encode, "saml", true, "saml(String str)"));
        tags.add(new Tag(Tag.Category.Encode, "base32", true, "base32_encode(String str)"));
        tags.add(new Tag(Tag.Category.Encode, "base58", true, "base58Encode(String str)"));
        tags.add(new Tag(Tag.Category.Encode, "base64", true, "base64Encode(String str)"));
        tags.add(new Tag(Tag.Category.Encode, "base64url", true, "base64urlEncode(String str)"));
        tags.add(new Tag(Tag.Category.Encode, "html_entities", true, "html_entities(String str)"));
        tags.add(new Tag(Tag.Category.Encode, "html5_entities", true, "html5_entities(String str)"));
        tag = new Tag(Tag.Category.Encode, "hex", true, "hex(String str, String separator)");
        tag.argument1 = new TagArgument("string", " ");
        tags.add(tag);
        tags.add(new Tag(Tag.Category.Encode, "hex_entities", true, "hex_entities(String str)"));
        tags.add(new Tag(Tag.Category.Encode, "hex_escapes", true, "hex_escapes(String str)"));
        tags.add(new Tag(Tag.Category.Encode, "octal_escapes", true, "octal_escapes(String str)"));
        tags.add(new Tag(Tag.Category.Encode, "dec_entities", true, "dec_entities(String str)"));
        tags.add(new Tag(Tag.Category.Encode, "unicode_escapes", true, "unicode_escapes(String str)"));
        tags.add(new Tag(Tag.Category.Encode, "css_escapes", true, "css_escapes(String Bstr)"));
        tags.add(new Tag(Tag.Category.Encode, "css_escapes6", true, "css_escapes6(String str)"));
        tags.add(new Tag(Tag.Category.Encode, "burp_urlencode", true, "burp_urlencode(String str)"));
        tags.add(new Tag(Tag.Category.Encode, "urlencode", true, "urlencode(String str)"));
        tags.add(new Tag(Tag.Category.Encode, "urlencode_not_plus", true, "urlencode_not_plus(String str)"));
        tags.add(new Tag(Tag.Category.Encode, "urlencode_all", true, "urlencode_all(String str)"));
        tags.add(new Tag(Tag.Category.Encode, "php_non_alpha", true, "php_non_alpha(String input)"));
        tags.add(new Tag(Tag.Category.Encode, "php_chr", true, "php_chr(String str)"));
        tags.add(new Tag(Tag.Category.Encode, "sql_hex", true, "sql_hex(String str)"));
        tag = new Tag(Tag.Category.Encode, "jwt", true, "jwt(String payload, String algo, String secret)");
        tag.argument1 = new TagArgument("string", "HS256");
        tag.argument2 = new TagArgument("string", "secret");
        tags.add(tag);
        tags.add(new Tag(Tag.Category.Encode, "powershell", true, "powershell(String cmd)"));
        tags.add(new Tag(Tag.Category.Encode, "quoted_printable", true, "quoted_printable(String str)"));
        tags.add(new Tag(Tag.Category.Encode, "js_string", true, "js_string(String str)"));
        tags.add(new Tag(Tag.Category.Decode, "d_saml", true, "d_saml(String str)"));
        tags.add(new Tag(Tag.Category.Decode, "auto_decode", true, "auto_decode(String str)"));
        tags.add(new Tag(Tag.Category.Decode, "auto_decode_no_decrypt", true, "auto_decode_no_decrypt(String str)"));
        tags.add(new Tag(Tag.Category.Decode, "d_base32", true, "decode_base32(String str)"));
        tags.add(new Tag(Tag.Category.Decode, "d_base58", true, "decode_base58(String str)"));
        tags.add(new Tag(Tag.Category.Decode, "d_base64", true, "decode_base64(String str)"));
        tags.add(new Tag(Tag.Category.Decode, "d_base64url", true, "decode_base64url(String str)"));
        tags.add(new Tag(Tag.Category.Decode, "d_html_entities", true, "decode_html_entities(String str)"));
        tags.add(new Tag(Tag.Category.Decode, "d_html5_entities", true, "decode_html5_entities(String str)"));
        tags.add(new Tag(Tag.Category.Decode, "d_js_string", true, "decode_js_string(String str)"));
        tags.add(new Tag(Tag.Category.Decode, "d_burp_url", true, "burp_decode_url(String str)"));
        tags.add(new Tag(Tag.Category.Decode, "d_url", true, "decode_url(String str)"));
        tags.add(new Tag(Tag.Category.Decode, "d_css_escapes", true, "decode_css_escapes(String str)"));
        tags.add(new Tag(Tag.Category.Decode, "d_octal_escapes", true, "decode_octal_escapes(String str)"));
        tags.add(new Tag(Tag.Category.Decode, "d_unicode_escapes", true, "decode_js_string(String str)"));
        tags.add(new Tag(Tag.Category.Decode, "d_jwt_get_payload", true, "d_jwt_get_payload(String token)"));
        tags.add(new Tag(Tag.Category.Decode, "d_jwt_get_header", true, "d_jwt_get_header(String token)"));
        tags.add(new Tag(Tag.Category.Decode, "d_quoted_printable", true, "d_quoted_printable(String str)"));
        tag = new Tag(Tag.Category.Decode, "json_parse", true, "json_parse(String json, String properties)");
        tag.argument1 = new TagArgument("string", "$property $propertyA-$propertyB");
        tags.add(tag);
        tag = new Tag(Tag.Category.Decode, "d_jwt_verify", true, "d_jwt_verify(String token, String secret)");
        tag.argument1 = new TagArgument("string", "secret");
        tags.add(tag);
        tag = new Tag(Tag.Category.Conditions, "if_regex", true, "if_regex(String str, String regex, String value)");
        tag.argument1 = new TagArgument("string", "regex");
        tag.argument2 = new TagArgument("string", "value");
        tags.add(tag);
        tag = new Tag(Tag.Category.Conditions, "if_not_regex", true, "if_not_regex(String str, String regex, String value)");
        tag.argument1 = new TagArgument("string", "regex");
        tag.argument2 = new TagArgument("string", "value");
        tags.add(tag);
        tags.add(new Tag(Tag.Category.Convert, "chunked_dec2hex", true, "chunked_dec2hex(String str)"));
        tag = new Tag(Tag.Category.Convert, "dec2hex", true, "dec2hex(String str, String regex)");
        tag.argument1 = new TagArgument("string", "(\\d+)");
        tags.add(tag);
        tag = new Tag(Tag.Category.Convert, "dec2oct", true, "dec2oct(String str, String regex)");
        tag.argument1 = new TagArgument("string", "(\\d+)");
        tags.add(tag);
        tag = new Tag(Tag.Category.Convert, "dec2bin", true, "dec2bin(String str, String regex)");
        tag.argument1 = new TagArgument("string", "(\\d+)");
        tags.add(tag);
        tag = new Tag(Tag.Category.Convert, "hex2dec", true, "hex2dec(String str, String regex)");
        tag.argument1 = new TagArgument("string", "((?:0x)?[a-f0-9]+)");
        tags.add(tag);
        tag = new Tag(Tag.Category.Convert, "oct2dec", true, "oct2dec(String str, String regex)");
        tag.argument1 = new TagArgument("string", "([0-7]+)");
        tags.add(tag);
        tag = new Tag(Tag.Category.Convert, "bin2dec", true, "bin2dec(String str, String regex)");
        tag.argument1 = new TagArgument("string", "([0-1]+)");
        tags.add(tag);
        tags.add(new Tag(Tag.Category.Convert, "ascii2bin", true, "ascii2bin(String str)"));
        tags.add(new Tag(Tag.Category.Convert, "bin2ascii", true, "bin2ascii(String str)"));
        tag = new Tag(Tag.Category.Convert, "ascii2hex", true, "ascii2hex(String str, String separator)");
        tag.argument1 = new TagArgument("string", " ");
        tags.add(tag);
        tags.add(new Tag(Tag.Category.Convert, "hex2ascii", true, "hex2ascii(String str)"));
        tags.add(new Tag(Tag.Category.Convert, "ascii2reverse_hex", true, "ascii2reverse_hex(String str, String separator)"));
        tags.add(new Tag(Tag.Category.String, "uppercase", true, "uppercase(String str)"));
        tags.add(new Tag(Tag.Category.String, "lowercase", true, "lowercase(String str)"));
        tags.add(new Tag(Tag.Category.String, "capitalise", true, "capitalise(String str)"));
        tags.add(new Tag(Tag.Category.String, "uncapitalise", true, "uncapitalise(String str)"));
        tags.add(new Tag(Tag.Category.String, "from_charcode", true, "from_charcode(String str)"));
        tags.add(new Tag(Tag.Category.String, "to_charcode", true, "to_charcode(String str)"));
        tags.add(new Tag(Tag.Category.String, "reverse", true, "reverse(String str)"));
        tags.add(new Tag(Tag.Category.String, "length", true, "len(String str)"));
        tags.add(new Tag(Tag.Category.String, "unique", true, "unique(String str)"));
        tag = new Tag(Tag.Category.String, "find", true, "find(String str, String find, int group)");
        tag.argument1 = new TagArgument("string", "find");
        tag.argument2 = new TagArgument("int", "-1");
        tags.add(tag);
        tag = new Tag(Tag.Category.String, "replace", true, "replace(String str, String find, String replace)");
        tag.argument1 = new TagArgument("string", "find");
        tag.argument2 = new TagArgument("string", "replace");
        tags.add(tag);
        tag = new Tag(Tag.Category.String, "regex_replace", true, "regex_replace(String str, String find, String replace)");
        tag.argument1 = new TagArgument("string", "find");
        tag.argument2 = new TagArgument("string", "replace");
        tags.add(tag);
        tag = new Tag(Tag.Category.String, "repeat", true, "repeat(String str, int amount)");
        tag.argument1 = new TagArgument("int", "100");
        tags.add(tag);
        tag = new Tag(Tag.Category.String, "substring", true, "substring(String str, int start, int end)");
        tag.argument1 = new TagArgument("int", "0");
        tag.argument2 = new TagArgument("int", "100");
        tags.add(tag);
        tag = new Tag(Tag.Category.String, "split_join", true, "split_join(String str, String splitChar, String joinChar)");
        tag.argument1 = new TagArgument("string", "split char");
        tag.argument2 = new TagArgument("string", "join char");
        tags.add(tag);

        Faker faker = new Faker();
        generateFakeTags(new Object[]{
                faker.address(),
                faker.ancient(),
                faker.animal(),
                faker.app(),
                faker.artist(),
                faker.avatar(),
                faker.aviation(),
                faker.book(),
                faker.bool(),
                faker.business(),
                faker.code(),
                faker.currency(),
                faker.color(),
                faker.commerce(),
                faker.company(),
                faker.crypto(),
                faker.date(),
                faker.demographic(),
                faker.educator(),
                faker.file(),
                faker.finance(),
                faker.food(),
                faker.hacker(),
                faker.idNumber(),
                faker.internet(),
                faker.job(),
                faker.lorem(),
                faker.music(),
                faker.name(),
                faker.nation(),
                faker.number(),
                faker.options(),
                faker.phoneNumber(),
                faker.slackEmoji(),
                faker.space(),
                faker.stock(),
                faker.team(),
                faker.university(),
                faker.weather()
        });

        tag = new Tag(Tag.Category.HMAC, "hmac_md5", true, "hmacmd5(String str, String key)");
        tag.argument1 = new TagArgument("string", "SECRET");
        tags.add(tag);
        tag = new Tag(Tag.Category.HMAC, "hmac_sha1", true, "hmacsha1(String str, String key)");
        tag.argument1 = new TagArgument("string", "SECRET");
        tags.add(tag);
        tag = new Tag(Tag.Category.HMAC, "hmac_sha224", true, "hmacsha224(String str, String key)");
        tag.argument1 = new TagArgument("string", "SECRET");
        tags.add(tag);
        tag = new Tag(Tag.Category.HMAC, "hmac_sha256", true, "hmacsha256(String str, String key)");
        tag.argument1 = new TagArgument("string", "SECRET");
        tags.add(tag);
        tag = new Tag(Tag.Category.HMAC, "hmac_sha384", true, "hmacsha384(String str, String key)");
        tag.argument1 = new TagArgument("string", "SECRET");
        tags.add(tag);
        tag = new Tag(Tag.Category.HMAC, "hmac_sha512", true, "hmacsha512(String str, String key)");
        tag.argument1 = new TagArgument("string", "SECRET");
        tags.add(tag);
        tags.add(new Tag(Tag.Category.Hash, "sha1", true, "sha1(String str)"));
        tags.add(new Tag(Tag.Category.Hash, "sha224", true, "sha224(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "sha256", true, "sha256(String str)"));
        tags.add(new Tag(Tag.Category.Hash, "sha384", true, "sha384(String str)"));
        tags.add(new Tag(Tag.Category.Hash, "sha512", true, "sha512(String str)"));
        tags.add(new Tag(Tag.Category.Hash, "sha3", true, "sha3(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "sha3_224", true, "sha3_224(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "sha3_256", true, "sha3_256(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "sha3_384", true, "sha3_384(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "sha3_512", true, "sha3_512(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "skein_256_128", true, "skein_256_128(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "skein_256_160", true, "skein_256_160(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "skein_256_224", true, "skein_256_224(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "skein_256_256", true, "skein_256_256(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "skein_512_128", true, "skein_512_128(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "skein_512_160", true, "skein_512_160(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "skein_512_224", true, "skein_512_224(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "skein_512_256", true, "skein_512_256(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "skein_512_384", true, "skein_512_384(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "skein_512_512", true, "skein_512_512(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "skein_1024_384", true, "skein_1024_384(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "skein_1024_512", true, "skein_1024_512(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "skein_1024_1024", true, "skein_1024_1024(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "sm3", true, "sm3(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "tiger", true, "tiger(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "md2", true, "md2(String str)"));
        tags.add(new Tag(Tag.Category.Hash, "md4", true, "md4(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "md5", true, "md5(String str)"));
        tags.add(new Tag(Tag.Category.Hash, "gost3411", true, "gost3411(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "ripemd128", true, "ripemd128(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "ripemd160", true, "ripemd160(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "ripemd256", true, "ripemd256(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "ripemd320", true, "ripemd320(String message)"));
        tags.add(new Tag(Tag.Category.Hash, "whirlpool", true, "whirlpool(String message)"));
        tag = new Tag(Tag.Category.Math, "range", true, "range(String str, int from, int to, int step)");
        tag.argument1 = new TagArgument("int", "0");
        tag.argument2 = new TagArgument("int", "100");
        tag.argument3 = new TagArgument("int", "1");
        tags.add(tag);
        tags.add(new Tag(Tag.Category.Math, "total", true, "total(String str)"));
        tag = new Tag(Tag.Category.Math, "arithmetic", true, "arithmetic(String str, int amount, String operation, String splitChar)");
        tag.argument1 = new TagArgument("int", "10");
        tag.argument2 = new TagArgument("string", "+");
        tag.argument3 = new TagArgument("string", ",");
        tags.add(tag);
        tag = new Tag(Tag.Category.Math, "convert_base", true, "convert_base(String str, String splitChar, int from, int to)");
        tag.argument1 = new TagArgument("string", ",");
        tag.argument2 = new TagArgument("int", "from");
        tag.argument3 = new TagArgument("int", "to");
        tags.add(tag);
        tag = new Tag(Tag.Category.Math, "random", true, "random(String chars, int len, Boolean everyCharOnce)");
        tag.argument1 = new TagArgument("int", "10");
        tag.argument2 = new TagArgument("boolean", "false");
        tags.add(tag);
        tag = new Tag(Tag.Category.Math, "random_alpha_lower", false, "random_alpha_lower(int len)");
        tag.argument1 = new TagArgument("int", "10");
        tags.add(tag);
        tag = new Tag(Tag.Category.Math, "random_alphanum_lower", false, "random_alphanum_lower(int len)");
        tag.argument1 = new TagArgument("int", "10");
        tags.add(tag);
        tag = new Tag(Tag.Category.Math, "random_alpha_upper", false, "random_alpha_upper(int len)");
        tag.argument1 = new TagArgument("int", "10");
        tags.add(tag);
        tag = new Tag(Tag.Category.Math, "random_alphanum_upper", false, "random_alphanum_upper(int len)");
        tag.argument1 = new TagArgument("int", "10");
        tags.add(tag);
        tag = new Tag(Tag.Category.Math, "random_alpha_mixed", false, "random_alpha_mixed(int len)");
        tag.argument1 = new TagArgument("int", "10");
        tags.add(tag);
        tag = new Tag(Tag.Category.Math, "random_alphanum_mixed", false, "random_alphanum_mixed(int len)");
        tag.argument1 = new TagArgument("int", "10");
        tags.add(tag);
        tag = new Tag(Tag.Category.Math, "random_hex", false, "random_hex(int len)");
        tag.argument1 = new TagArgument("int", "10");
        tags.add(tag);
        tag = new Tag(Tag.Category.Math, "random_hex_mixed", false, "random_hex_mixed(int len)");
        tag.argument1 = new TagArgument("int", "10");
        tags.add(tag);
        tag = new Tag(Tag.Category.Math, "random_num", false, "random_num(int len)");
        tag.argument1 = new TagArgument("int", "10");
        tags.add(tag);
        tag = new Tag(Tag.Category.Math, "random_unicode", false, "random_unicode(int from, int to, int amount)");
        tag.argument1 = new TagArgument("int", "0");
        tag.argument2 = new TagArgument("int", "0xffff");
        tag.argument3 = new TagArgument("int", "100");
        tags.add(tag);
        tag = new Tag(Tag.Category.Math, "zeropad", true, "zeropad(String str, String splitChar, int amount)");
        tag.argument1 = new TagArgument("string", ",");
        tag.argument2 = new TagArgument("int", "2");
        tags.add(tag);
        tag = new Tag(Tag.Category.Math, "uuid", false, "uuid()");
        tags.add(tag);
        tags.add(new Tag(Tag.Category.XSS, "behavior", true, "behavior(String str)"));
        tags.add(new Tag(Tag.Category.XSS, "css_expression", true, "css_expression(String str)"));
        tags.add(new Tag(Tag.Category.XSS, "datasrc", true, "datasrc(String str)"));
        tags.add(new Tag(Tag.Category.XSS, "eval_fromcharcode", true, "eval_fromcharcode(String str)"));
        tags.add(new Tag(Tag.Category.XSS, "iframe_data_url", true, "iframe_data_url(String str)"));
        tags.add(new Tag(Tag.Category.XSS, "iframe_src_doc", true, "iframe_src_doc(String str)"));
        tags.add(new Tag(Tag.Category.XSS, "script_data", true, "script_data(String str)"));
        tags.add(new Tag(Tag.Category.XSS, "uppercase_script", true, "uppercase_script(String str)"));
        tags.add(new Tag(Tag.Category.XSS, "template_eval", true, "template_eval(String str)"));
        tags.add(new Tag(Tag.Category.XSS, "throw_eval", true, "throw_eval(String str)"));
        Tag setTag = new Tag(Tag.Category.Variables, "set_variable1", true, "Special tag that lets you store the results of a conversion. Change variable1 to your own variable name. The argument specifies if the variable is global.");
        setTag.argument1 = new TagArgument("boolean", "false");
        tags.add(setTag);
        tags.add(new Tag(Tag.Category.Variables, "get_variable1", false, "Special tag that lets you get a previously set variable. Change var to your own variable name."));
        tag = new Tag(Tag.Category.Variables, "context_url", false, "context_url(String properties");
        tag.argument1 = new TagArgument("string", "$protocol $host $path $file $query $port");
        tags.add(tag);
        tag = new Tag(Tag.Category.Variables, "context_body", false, "context_body()");
        tags.add(tag);
        tag = new Tag(Tag.Category.Variables, "context_header", false, "context_url(String headerName");
        tag.argument1 = new TagArgument("string", "$headerName");
        tags.add(tag);
        tag = new Tag(Tag.Category.Variables, "context_param", false, "context_url(String paramName");
        tag.argument1 = new TagArgument("string", "$paramName");
        tags.add(tag);
        tag = new Tag(Tag.Category.Languages, "python", true, "python(String input, String code, String codeExecuteKey)");
        tag.argument1 = new TagArgument("string", "output = input.upper()");
        tag.argument2 = new TagArgument("string", HackvertorExtension.tagCodeExecutionKey);
        tags.add(tag);
        tag = new Tag(Tag.Category.Languages, "javascript", true, "javascript(String input, String code, String codeExecuteKey)");
        tag.argument1 = new TagArgument("string", "output = input.toUpperCase()");
        tag.argument2 = new TagArgument("string", HackvertorExtension.tagCodeExecutionKey);
        tags.add(tag);
        tag = new Tag(Tag.Category.Languages, "java", true, "java(String input, String code, String codeExecuteKey)");
        tag.argument1 = new TagArgument("string", "output = input.toUpperCase()");
        tag.argument2 = new TagArgument("string", HackvertorExtension.tagCodeExecutionKey);
        tags.add(tag);
        tag = new Tag(Tag.Category.Languages, "groovy", true, "groovy(String input, String code, String codeExecuteKey)");
        tag.argument1 = new TagArgument("string", "output = input.toUpperCase()");
        tag.argument2 = new TagArgument("string", HackvertorExtension.tagCodeExecutionKey);
        tags.add(tag);
        tag = new Tag(Tag.Category.System, "read_url", true, "real_url(String url, String charset, String codeExecuteKey)");
        tag.argument1 = new TagArgument("string", "UTF-8");
        tag.argument2 = new TagArgument("boolean", "false");
        tag.argument3 = new TagArgument("string", HackvertorExtension.tagCodeExecutionKey);
        tags.add(tag);
        tag = new Tag(Tag.Category.System, "system", true, "system(String cmd, Boolean enabled, String codeExecuteKey)");
        tag.argument1 = new TagArgument("boolean", "false");
        tag.argument2 = new TagArgument("string", HackvertorExtension.tagCodeExecutionKey);
        tags.add(tag);
        for (int j = 0; j < customTags.length(); j++) {
            JSONObject customTag = (JSONObject) customTags.get(j);
            tag = HackvertorExtension.generateCustomTag(customTag);
            tags.add(tag);
        }

    }

    public void generateFakeTags(Object[] fakeObjects) {
        for(int i=0;i<fakeObjects.length;i++) {
            Object fakeObject = fakeObjects[i];
            String name = Convertors.lowercaseFirst(fakeObject.getClass().getSimpleName());
            Tag tag = new Tag(Tag.Category.Fake, "fake_" + name, false, name + "(String properties, String locale)");
            Method[] methods = fakeObject.getClass().getDeclaredMethods();
            ArrayList<String> properties = new ArrayList<>();
            for (Method method : methods) {
                if (shouldFilterMethod(method)) {
                    continue;
                }
                properties.add('$' + method.getName());
            }
            tag.argument1 = new TagArgument("string", String.join(", ", properties));
            tag.argument2 = new TagArgument("string", "en-GB");
            tags.add(tag);
        }
    }

    public static Boolean shouldFilterMethod(Method method) {
        return method.getParameterCount() != 0 || !Modifier.isPublic(method.getModifiers());
    }

    public JSONArray getCustomTags() {
        return customTags;
    }

    public void setCustomTags(JSONArray tags) {
        this.customTags = tags;
    }

    public String convert(String message, Hackvertor hackvertor){
        return Convertors.weakConvert(new HashMap<>(), customTags, message, hackvertor);
    }

    public ArrayList<Tag> getTags() {
        ArrayList<Tag> tagsAndCustom = new ArrayList<>(tags);
        for (int j = 0; j < customTags.length(); j++) {
            JSONObject customTag = (JSONObject) customTags.get(j);
            Tag tag = HackvertorExtension.generateCustomTag(customTag);
            tagsAndCustom.add(tag);
        }
        tagsAndCustom.sort(Comparator.comparing(o -> o.name));
        return tagsAndCustom;
    }
}
